package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.ui.view_model.ai_handling.AndroidCameraController
import com.frontend.nutricheck.client.ui.view_model.ai_handling.CameraController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that provides bindings for camera and image processing dependencies.
 *
 * This module binds the Android-specific implementations to their respective interfaces,
 * enabling dependency injection throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
interface ViewModelModule {

    @Binds
    fun bindCameraController(
        androidCameraController: AndroidCameraController
    ): CameraController
}