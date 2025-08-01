package com.frontend.nutricheck.client.ui.view_model.add_components

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
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
            else -> { /* other events are for Navigation or UI updates, handled in the UI layer */ }
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
        try { awaitCancellation() } finally { processCameraProvider.unbindAll() }
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
                    // JPEG in PNG konvertieren
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
    private fun convertJpegToPng(uri: Uri, contentResolver: ContentResolver): Uri? {
        return try {
            // JPEG-Bild in Bitmap laden
            val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return null

            // Neue Datei für PNG erstellen
            val name = "${System.currentTimeMillis()}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NutriCheck")
            }

            // PNG-Datei speichern
            val pngUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            pngUri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                bitmap.recycle() // Speicher freigeben
                return it
            }
            null
        } catch (e: Exception) {
            Log.e("ConvertJpegToPng", "Error converting JPEG to PNG: $uri", e)
            null
        }
    }
    private fun submitPhoto() {
        viewModelScope.launch {
            setLoading()
            val multipartBody = uriToMultipartBody(_photoUri.value, appContext.contentResolver, appContext)
            if (multipartBody == null) {
                setError(appContext.getString(R.string.error_encoding_image))
                _photoUri.value = null
                return@launch
            }
            val response = historyRepository.requestAiMeal(multipartBody)
            val dayTime = DayTime.dateToDayTime(Date())
            if (response is Result.Success) {
                val meal = response.data
                val mealCopy = meal.copy(dayTime = dayTime)
                emitEvent(AddAiMealEvent.ShowMealOverview(
                    mealCopy.id, mealCopy.mealFoodItems.first().foodProduct.id))
            } else if (response is Result.Error) {
                Log.e("SubmitPhoto", "API error: ${response.message}")
                setError(appContext.getString(R.string.error_encoding_image))
                _photoUri.value = null
                return@launch
            }
            _photoUri.value = null
            setReady()
        }
    }private fun retakePhoto() {
        _photoUri.value = null
    }
    // parse the image URI to a MultipartBody.Part for sending to the backend
    /**
     * Konvertiert ein Bild von einer Uri in ein MultipartBody.Part für den API-Upload.
     * Wenn das Bild im JPEG-Format vorliegt, wird es in PNG konvertiert, da der Server PNG erwartet.
     *
     * @param uri Die Uri des Bildes (z. B. aus der Kamera oder Galerie).
     * @param contentResolver Der ContentResolver für den Zugriff auf die Uri.
     * @param context Der Android-Kontext für den Zugriff auf Ressourcen.
     * @return Ein MultipartBody.Part für den Upload oder null, wenn ein Fehler auftritt.
     */
    fun uriToMultipartBody(uri: Uri?, contentResolver: ContentResolver, context: android.content.Context): MultipartBody.Part? {
        if (uri == null) return null

        val partName = "file"
        val defaultFileName = "upload.png"

        return try {
            // Prüfen, ob das Bild bereits PNG ist
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val pngUri = if (mimeType != "image/png") {
                // JPEG in PNG konvertieren
                convertJpegToPng(uri, contentResolver, context)
            } else {
                uri
            } ?: return null

            // Dateinamen ermitteln
            val fileName = getFileNameFromUri(pngUri, contentResolver) ?: defaultFileName

            // RequestBody erstellen
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

            // MultipartBody.Part erstellen
            MultipartBody.Part.createFormData(partName, fileName, requestBody)
        } catch (e: Exception) {
            Log.e("UriToMultipartBody", "Unexpected error processing URI: $uri", e)
            null
        }
    }

    /**
     * Konvertiert ein JPEG-Bild von einer Uri in ein PNG-Bild und gibt die neue Uri zurück.
     *
     * @param uri Die Uri des JPEG-Bildes.
     * @param contentResolver Der ContentResolver für den Zugriff auf die Uri.
     * @param context Der Android-Kontext für den Zugriff auf den MediaStore.
     * @return Die Uri des neuen PNG-Bildes oder null, wenn ein Fehler auftritt.
     */
    private fun convertJpegToPng(uri: Uri, contentResolver: ContentResolver, context: android.content.Context): Uri? {
        return try {
            // JPEG-Bild in Bitmap laden
            val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return null

            // Neue Datei für PNG erstellen
            val name = "${System.currentTimeMillis()}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NutriCheck")
            }

            // PNG-Datei speichern
            val pngUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            pngUri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                bitmap.recycle() // Speicher freigeben
                return it
            }
            null
        } catch (e: Exception) {
            Log.e("ConvertJpegToPng", "Error converting JPEG to PNG: $uri", e)
            null
        }
    }

    /**
     * Ermittelt den Dateinamen aus einer Uri und stellt sicher, dass er mit .png endet.
     *
     * @param uri Die Uri des Bildes.
     * @param contentResolver Der ContentResolver für den Zugriff auf die Uri.
     * @return Der Dateiname oder null, wenn er nicht ermittelt werden kann.
     */
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