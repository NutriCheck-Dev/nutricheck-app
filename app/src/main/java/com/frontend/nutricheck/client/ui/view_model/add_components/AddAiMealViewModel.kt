package com.frontend.nutricheck.client.ui.view_model.add_components

import android.util.Base64
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
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


sealed interface AddAiMealEvent {
    object OnRetakePhoto : AddAiMealEvent
    object OnSubmitPhoto : AddAiMealEvent
    object OnTakePhoto : AddAiMealEvent
    object ResetErrorState : AddAiMealEvent

    object ShowMealOverview : AddAiMealEvent

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
 * @property remoteRepository Remote repository for API interactions.
 */
@HiltViewModel
class AddAiMealViewModel @Inject constructor(
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
                    _photoUri.value = result.savedUri
                }
                override fun onError (exception: ImageCaptureException) {
                    _photoUri.value = null
                    setError(appContext.getString(R.string.error_no_photo_taken))
                }
            }
        )
    }

    private fun submitPhoto() {
        val uri = _photoUri.value
        viewModelScope.launch {
            setLoading()
            // convert the image to
            // request from server
            // parse the response
            // copy from meal and parse daytime
            // copy to DB
            // aufruf von foodproductoverview mit mealID
            // view zerstören sonst wieder camera
            _photoUri.value = null
            setReady()
        }
    }

    private fun retakePhoto() {
        _photoUri.value = null
    }
    private fun emitEvent(event: AddAiMealEvent) = viewModelScope.launch { _events.emit(event) }
}