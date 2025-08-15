package com.frontend.nutricheck.client.ui.view_model.add_components

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepositoryImpl
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.IOException


/**
 * All possible events for AI-based meal estimation.
 * Events are used for communication between the UI and ViewModel.
 */
sealed interface AddAiMealEvent {
    object OnRetakePhoto : AddAiMealEvent
    object OnSubmitPhoto : AddAiMealEvent
    object OnTakePhoto : AddAiMealEvent
    object ResetErrorState : AddAiMealEvent

    data class ShowMealOverview(val mealId : String, val foodProductId: String) : AddAiMealEvent

}
/**
 * ViewModel for handling AI-based meal estimation using camera input.
 * Manages camera preview, photo capture, and communication with the backend API.
 *
 * Responsibilities:
 * - Binds camera preview to lifecycle.
 * - Captures photos with proper EXIF handling.
 * - Converts images to PNG format
 * - Sends the encoded image to the backend for meal estimation.
 * - Validates AI meal detection results.
 *
 * @property appContext Application context for accessing resources and content resolver.
 * @property historyRepository Remote repository for API interactions and meal history management.
 */
@HiltViewModel
class AddAiMealViewModel @Inject constructor(
    private val historyRepository: HistoryRepositoryImpl,
    @ApplicationContext private val appContext: Context
) : BaseViewModel() {

    companion object {
        private const val MIME_TYPE_JPEG = "image/jpeg"
        private const val MIME_TYPE_PNG = "image/png"

        // Minimum nutritional values to consider a meal valid
        private const val MIN_NUTRITIONAL_VALUE = 0.0
    }
    // Camera-related state
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    // Photo-related state
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    private val _events = MutableSharedFlow<AddAiMealEvent>()
    val events: SharedFlow<AddAiMealEvent> = _events.asSharedFlow()

    // Camera use cases
    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    private val imageCaptureUseCase = ImageCapture.Builder().build()

    /**
     * Handles UI events which need to be processed by the ViewModel.
     *
     * @param event The event to handle.
     */
    fun onEvent(event: AddAiMealEvent) {
        when (event) {
            is AddAiMealEvent.OnRetakePhoto -> retakePhoto()
            is AddAiMealEvent.OnSubmitPhoto -> submitPhoto()
            is AddAiMealEvent.OnTakePhoto -> takePhoto()
            is AddAiMealEvent.ResetErrorState -> setReady()
            else -> { /* other events are for Navigation or UI updates, handled in the UI layer */ }
        }
    }

    /**
     * Binds the camera preview and image capture use cases to the given lifecycle owner.
     *
     * @param appContext The application context.
     * @param lifecycleOwner The lifecycle owner to bind the camera to.
     *
     */
    suspend fun bindToCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        try {
            val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                cameraPreviewUseCase,
                imageCaptureUseCase
            )
            awaitCancellation()
        } finally {
            // Ensure camera resources are properly released
            ProcessCameraProvider.getInstance(context).get().unbindAll()
        }
    }

    /**
     * Handles photo capture process.
     */
    private fun takePhoto() {
        // Generate a unique filename for the photo
        val name = "${System.currentTimeMillis()}.jpg"
        // Prepare content values for MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE_JPEG)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NutriCheck")
        }
        // Create output options for the image capture
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
                appContext.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

        imageCaptureUseCase.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(appContext),
            //Creates the callback for handling image capture results
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    val jpegUri = result.savedUri
                    _photoUri.value = jpegUri ?: run {
                        setError(appContext.getString(R.string.error_no_photo_taken))
                        null
                    }
                }
                override fun onError(exception: ImageCaptureException) {
                    _photoUri.value = null
                    setError(appContext.getString(R.string.error_no_photo_taken))
                }
            }
        )
    }

    /**
     * Handles the submission of the captured photo for AI-based meal estimation.
     * Also validates the meal data received from the backend.
     */
    private fun submitPhoto() {
        viewModelScope.launch {
            setLoading()
            val multipartBody =
                convertUriToMultipartBody(_photoUri.value)
            if (multipartBody == null) {
                setError(appContext.getString(R.string.error_encoding_image))
                _photoUri.value = null
                return@launch
            }
            val response = historyRepository.requestAiMeal(multipartBody)
            handleApiResponse(response)
        }
    }

    /**
     * Handles the API response from the AI meal estimation request.
     * @param response The result of the API call containing the meal data or an error.
     */
    private suspend fun handleApiResponse(response: Result<Meal>) {
        when (response) {
            is Result.Success -> {
                val meal = response.data
                if (meal.isFoodDetected()) {
                    historyRepository.addMeal(meal)
                    setReady()
                    // Emit event to show meal overview with the food product ID
                    emitEvent(
                        AddAiMealEvent.ShowMealOverview(
                            meal.id, meal.mealFoodItems.first().foodProduct.id
                        )
                    )
                } else {
                    setError(appContext.getString(R.string.error_no_food_detected))
                    _photoUri.value = null
                }
            }
            is Result.Error -> {
                setError(appContext.getString(R.string.error_encoding_image))
                _photoUri.value = null
            }
        }
    }

    /**
     * Resets the photo URI to allow retaking the photo.
     */
    private fun retakePhoto() {
        _photoUri.value = null
    }
    /**
     * Extension function to check if a meal contains detected food.
     * Food is considered detected if all nutritional values are greater than zero.
     * @return true if food is detected, false otherwise
     */
    private fun Meal.isFoodDetected(): Boolean {
        val firstFoodProduct = this.mealFoodItems.firstOrNull()?.foodProduct
        return firstFoodProduct?.let { product ->
            product.calories > MIN_NUTRITIONAL_VALUE &&
                    product.carbohydrates > MIN_NUTRITIONAL_VALUE &&
                    product.protein > MIN_NUTRITIONAL_VALUE &&
                    product.fat > MIN_NUTRITIONAL_VALUE
        } == true
    }

    /**
    * Converts a URI to MultipartBody.Part for API transmission.
    * Handles image processing including format conversion and rotation correction.
    *
    * @param uri The image URI to convert
    * @return MultipartBody.Part or null if conversion fails
    */
    private fun convertUriToMultipartBody(uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null
        return try {
            val contentResolver = appContext.contentResolver
            val mimeType = contentResolver.getType(uri) ?: MIME_TYPE_JPEG

            // Process image based on type
            val (processedUri, finalMimeType) = when {
                mimeType == MIME_TYPE_PNG -> {
                    // PNG files don't need processing
                    Pair(uri, MIME_TYPE_PNG)
                }
                mimeType.startsWith("image/") -> {
                    // Process JPEG and other image formats
                    val processedUri = processImageWithRotation(uri, contentResolver)
                    Pair(processedUri ?: uri, if (processedUri != null) MIME_TYPE_PNG else mimeType)
                }
                else -> {
                    // Fallback for unknown types
                    Pair(uri, mimeType)
                }
            }
            createMultipartBodyPart(processedUri, finalMimeType, contentResolver)
        } catch (e: Exception) {
            Log.e("ConvertUriToMultipartBody", "Error converting URI to MultipartBody: $uri", e)
            null
        }
    }
    /**
     * Processes JPEG images by applying EXIF rotation and converting to PNG.
     * This ensures consistent image orientation regardless of device rotation.
     *
     * @param uri The original image URI
     * @param contentResolver ContentResolver for accessing the image
     * @return Processed image URI or null if processing fails
     */
    private fun processImageWithRotation(uri: Uri, contentResolver: ContentResolver): Uri? {
        return try {
            // Load the bitmap from URI
            val originalBitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return null
            // Get EXIF orientation from the image
            val orientation = contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
            // Apply rotation if needed
            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
                else -> originalBitmap
            }
            // Save as PNG to avoid quality loss
            val pngUri = saveBitmapAsPng(rotatedBitmap, contentResolver)
            // Clean up memory
            if (rotatedBitmap != originalBitmap) {
                originalBitmap.recycle()
            }
            rotatedBitmap.recycle()
            pngUri
        } catch (e: Exception) {
            Log.e("ProcessImageWithRotation", "Error processing image with rotation: $uri", e)
            null
        }
    }

    /**
     * Rotates a bitmap by the specified degrees.
     */
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Saves a bitmap as PNG to MediaStore.
     * @param bitmap The bitmap to save
     * @param contentResolver ContentResolver for access
     * @return URI of saved image or null if failed
     */
    private fun saveBitmapAsPng(bitmap: Bitmap, contentResolver: ContentResolver): Uri? {
        val name = "${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE_PNG)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NutriCheck")
        }

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues)?.also { pngUri ->
            contentResolver.openOutputStream(pngUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
    }
    /**
     * Creates a MultipartBody.Part from URI and MIME type for API transmission.
     * @param uri The image URI
     * @param mimeType The MIME type of the image
     * @param contentResolver ContentResolver for accessing the image
     * @return MultipartBody.Part or null if creation fails
     */
    private fun createMultipartBodyPart(
        uri: Uri,
        mimeType: String,
        contentResolver: ContentResolver
    ): MultipartBody.Part? {
        val partName = "file"
        val fileName = getFileNameFromUri(uri, contentResolver) ?: "upload.${getFileExtension(mimeType)}"
        // Creates a RequestBody from URI for multipart upload
        val requestBody = object : RequestBody() {
            override fun contentType() = mimeType.toMediaTypeOrNull() ?: "application/octet-stream".toMediaType()
            //Gets the file size from URI
            override fun contentLength(): Long =
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (sizeIndex != -1) cursor.getLong(sizeIndex) else -1
                    } else -1
                } ?: -1

            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(sink.outputStream())
                } ?: throw IOException("Failed to open InputStream for URI: $uri")
            }
        }

        return MultipartBody.Part.createFormData(partName, fileName, requestBody)
    }
    /**
     * Gets the display name of a file from its URI.
     * @param uri The file URI
     * @param contentResolver ContentResolver for access
     * @return File name or null if not available
     */
    private fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) cursor.getString(nameIndex) else null
            } else null
        }
    }
    /**
     * Gets appropriate file extension for MIME type.
     * @param mimeType The MIME type
     * @return File extension without dot
     */
    private fun getFileExtension(mimeType: String): String = when (mimeType) {
        MIME_TYPE_PNG -> "png"
        MIME_TYPE_JPEG, "image/jpg" -> "jpg"
        else -> "jpg"
    }
    /**
     * Emits an event to the shared flow for UI consumption.
     * @param event The event to emit
     */
    private fun emitEvent(event: AddAiMealEvent) = viewModelScope.launch {
        _events.emit(event)
    }
}