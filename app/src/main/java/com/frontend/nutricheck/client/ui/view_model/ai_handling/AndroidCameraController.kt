package com.frontend.nutricheck.client.ui.view_model.ai_handling

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.frontend.nutricheck.client.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
/**
 * Implementation of [CameraController] using CameraX.
 *
 * Provides camera preview and photo capture functionality while managing
 * camera lifecycle and resource cleanup automatically.
 *
 * @property appContext Application context for accessing system resources and content resolver
 */
@Singleton
class AndroidCameraController @Inject constructor(
    @ApplicationContext private val appContext: Context
) : CameraController {

    companion object {
        private const val MIME_TYPE_JPEG = "image/jpeg"
    }

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    override val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    /**
     * Camera preview use case that provides surface requests for UI rendering.
     */
    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }
    // Image capture use case configured for high-quality photo capture
    private val imageCaptureUseCase = ImageCapture.Builder().build()

    override suspend fun bindToCamera(context: Context, lifecycleOwner: LifecycleOwner) {
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
            ProcessCameraProvider.getInstance(context).get().unbindAll()
        }
    }

    override fun takePhoto(onSuccess: (Uri?) -> Unit, onError: (String) -> Unit) {
        val tempFile = File.createTempFile("photo_${System.currentTimeMillis()}",
            ".jpg", appContext.cacheDir)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        imageCaptureUseCase.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(appContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(tempFile)
                    onSuccess(savedUri ?: run {
                        onError(appContext.getString(R.string.error_no_photo_taken))
                        null
                    })
                }
                override fun onError(exception: ImageCaptureException) {
                    tempFile.delete()
                    onError(appContext.getString(R.string.error_no_photo_taken))
                }
            }
        )
    }
}
