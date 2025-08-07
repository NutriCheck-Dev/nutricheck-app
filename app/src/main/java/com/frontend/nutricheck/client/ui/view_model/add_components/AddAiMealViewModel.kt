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
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
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
import java.util.Date


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
 * - Captures photos and encodes them as Base64.
 * - Sends the encoded image to the backend for meal estimation.
 * - Emits UI events for navigation and error handling.
 *
 * @property appContext Application context for accessing resources and content resolver.
 * @property historyRepository Remote repository for API interactions.
 */
@HiltViewModel
class AddAiMealViewModel @Inject constructor(
    private val historyRepository: HistoryRepositoryImpl,
    @ApplicationContext private val appContext: Context
) : BaseViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    private val _events = MutableSharedFlow<AddAiMealEvent>()
    val events: SharedFlow<AddAiMealEvent> = _events.asSharedFlow()

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
            else -> { /* other events are for Navigation or UI updates, handled in the UI layer */
            }
        }
    }

    /**
     * Binds the camera preview and image capture use cases to the given lifecycle owner.
     *
     * @param appContext The application context.
     * @param lifecycleOwner The lifecycle owner to bind the camera to.
     */
    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        val selector = CameraSelector.DEFAULT_BACK_CAMERA
        processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            selector,
            cameraPreviewUseCase,
            imageCaptureUseCase
        )
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    private fun takePhoto() {
        val name = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NutriCheck")
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                appContext.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCaptureUseCase.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(appContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    val jpegUri = result.savedUri
                    // convert jpeg to png if necessary
                    val pngUri = jpegUri?.let { convertJpegToPng(it, appContext.contentResolver) }
                    _photoUri.value = pngUri ?: run {
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

    private fun submitPhoto() {
        viewModelScope.launch {
            setLoading()
            val multipartBody =
                uriToMultipartBody(_photoUri.value, appContext.contentResolver)
            if (multipartBody == null) {
                setError(appContext.getString(R.string.error_encoding_image))
                _photoUri.value = null
                return@launch
            }
            val response = historyRepository.requestAiMeal(multipartBody)
            val dayTime = DayTime.dateToDayTime(Date())
            if (response is Result.Success) {
                val meal = response.data
                if (!meal.isFoodDetected) {
                    setError(appContext.getString(R.string.error_no_food_detected))
                    _photoUri.value = null
                    return@launch
                }
                val mealCopy = meal.copy(dayTime = dayTime)
                historyRepository.addMeal(mealCopy)
                setReady()
                emitEvent(
                    AddAiMealEvent.ShowMealOverview(
                        mealCopy.id, mealCopy.mealFoodItems.first().foodProduct.id
                    )
                )
            } else if (response is Result.Error) {
                Log.e("SubmitPhoto", "API error: ${response.message}")
                setError(appContext.getString(R.string.error_encoding_image))
                _photoUri.value = null
                return@launch
            }
        }
    }
    private fun retakePhoto() {
        _photoUri.value = null
    }

    private val Meal.isFoodDetected: Boolean
        get() = (this.mealFoodItems.firstOrNull()?.foodProduct?.calories ?: 0.0) > 0.0 &&
                (this.mealFoodItems.firstOrNull()?.foodProduct?.carbohydrates ?: 0.0) > 0.0 &&
                (this.mealFoodItems.firstOrNull()?.foodProduct?.protein ?: 0.0) > 0.0 &&
                (this.mealFoodItems.firstOrNull()?.foodProduct?.fat ?: 0.0) > 0.0

    // parse the image URI to a MultipartBody.Part for sending to the backend
    private fun uriToMultipartBody(uri: Uri?, contentResolver: ContentResolver): MultipartBody.Part? {
        if (uri == null) return null

        val partName = "file"
        val defaultFileName = "upload.png"

        return try {
            // test if the URI is a valid image
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val pngUri = if (mimeType != "image/png") {
                // convert JPEG to PNG
                convertJpegToPng(uri, contentResolver)
            } else {
                uri
            } ?: return null

            // create a file name for the multipart part
            val fileName = getFileNameFromUri(pngUri, contentResolver) ?: defaultFileName
            val requestBody = object : RequestBody() {
                override fun contentType() = "image/png".toMediaTypeOrNull() ?: "application/octet-stream".toMediaType()
                override fun contentLength(): Long =
                    contentResolver.query(pngUri, null, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                            if (sizeIndex != -1) cursor.getLong(sizeIndex) else -1
                        } else -1
                    } ?: -1

                override fun writeTo(sink: BufferedSink) {
                    contentResolver.openInputStream(pngUri)?.use { inputStream ->
                        inputStream.copyTo(sink.outputStream())
                    } ?: throw IOException("Failed to open InputStream for URI: $pngUri")
                }
            }
            // create the MultipartBody.Part
            MultipartBody.Part.createFormData(partName, fileName, requestBody)
        } catch (e: Exception) {
            Log.e("UriToMultipartBody", "Unexpected error processing URI: $uri", e)
            null
        }
    }

    private fun convertJpegToPng(uri: Uri, contentResolver: ContentResolver): Uri? {
        return try {
            // JPEG-Bild in Bitmap laden
            val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return null
            // Read EXIF orientation
            val orientation = contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            // Rotate the bitmap if necessary
            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap
            }
            // Create a new file for PNG
            val name = "${System.currentTimeMillis()}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NutriCheck")
            }
            // Save the PNG file
            val pngUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            pngUri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                rotatedBitmap.recycle() // Free memory
                if (rotatedBitmap != bitmap) bitmap.recycle() // Recycle original bitmap if rotated
                return it
            }
            bitmap.recycle() // Recycle original bitmap if no PNG was created
            null
        } catch (e: Exception) {
            Log.e("ConvertJpegToPng", "Error converting JPEG to PNG: $uri", e)
            null
        }
    }
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    private fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    cursor.getString(nameIndex).let {
                        if (!it.endsWith(".png", ignoreCase = true)) {
                            it.substringBeforeLast(".", it) + ".png"
                        } else {
                            it
                        }
                    }
                } else {
                    null
                }
            } else {
                null
            }
        }
    }
    private fun emitEvent(event: AddAiMealEvent) = viewModelScope.launch { _events.emit(event) }
}