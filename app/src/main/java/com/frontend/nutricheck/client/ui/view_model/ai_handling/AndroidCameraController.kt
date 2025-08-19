package com.frontend.nutricheck.client.ui.view_model.ai_handling

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
import com.frontend.nutricheck.client.R
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
                    onSuccess(jpegUri ?: run {
                        onError(appContext.getString(R.string.error_no_photo_taken))
                        null
                    })
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(appContext.getString(R.string.error_no_photo_taken))
                }
            }
        )
    }
}
