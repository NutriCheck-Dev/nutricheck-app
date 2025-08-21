package com.frontend.nutricheck.client.ui.view_model.ai_handling

import android.content.Context
import android.net.Uri
import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for controlling camera operations including preview binding and photo capture.
 *
 * Abstracts CameraX functionality to enable testable camera operations in ViewModels
 * by providing a clean separation between Android-specific camera code and business logic.
 */
interface CameraController {
    /**
     * Binds camera preview and capture use cases to the provided lifecycle.
     *
     * @param context Application context for camera provider access
     * @param lifecycleOwner Lifecycle owner to bind camera operations to
     */
    suspend fun bindToCamera(context: Context, lifecycleOwner: LifecycleOwner)
    /**
     * Captures a photo and provides the result through callbacks.
     *
     * @param onSuccess Callback invoked with the URI of the captured photo, or null if capture failed
     * @param onError Callback invoked with error message if photo capture fails
     */
    fun takePhoto(onSuccess: (Uri?) -> Unit, onError: (String) -> Unit)
    /**
     * StateFlow providing the current camera surface request for preview rendering.
     *
     * @return StateFlow containing the SurfaceRequest for camera preview, or null if not available
     */
    val surfaceRequest: StateFlow<SurfaceRequest?>
}
