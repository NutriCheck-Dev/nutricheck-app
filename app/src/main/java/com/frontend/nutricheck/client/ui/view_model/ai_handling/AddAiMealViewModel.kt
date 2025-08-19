package com.frontend.nutricheck.client.ui.view_model.ai_handling

import android.content.Context
import android.app.Application
import android.net.Uri
import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddAiMealEvent {
    object OnRetakePhoto : AddAiMealEvent
    object OnSubmitPhoto : AddAiMealEvent
    object OnTakePhoto : AddAiMealEvent
    object ResetErrorState : AddAiMealEvent

    data class ShowMealOverview(val mealId : String, val foodProductId: String) : AddAiMealEvent
}
/**
 * ViewModel for handling AI-based meal estimation using camera input.
 *
 *  @property historyRepository Remote repository for API interactions and meal history management.
 *  @property imageProcessor Utility for processing images, converting URIs to multipart bodies.
 *  @property cameraController Controller for camera operations, including preview binding and photo capture.
 */
@HiltViewModel
class AddAiMealViewModel @Inject constructor(
    application: Application,
    private val historyRepository: HistoryRepository,
    private val imageProcessor: ImageProcessor,
    private val cameraController: CameraController
) : BaseViewModel() {

    private val appContext = application.applicationContext

    companion object {
        // Minimum nutritional values to consider a food product valid
        private const val MIN_NUTRITIONAL_VALUE = 0.0
    }

    // Camera-related state - delegated to CameraController
    val surfaceRequest: StateFlow<SurfaceRequest?> = cameraController.surfaceRequest

    // Photo-related state
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    private val _events = MutableSharedFlow<AddAiMealEvent>()
    val events: SharedFlow<AddAiMealEvent> = _events.asSharedFlow()
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
            else -> { /* other events are emitted by the ViewModel */ }
        }
    }
    /**
     * Binds the camera preview and image capture use cases to the given lifecycle owner.
     *
     * @param context The application context.
     * @param lifecycleOwner The lifecycle owner to bind the camera to.
     */
    suspend fun bindToCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        cameraController.bindToCamera(context, lifecycleOwner)
    }
    /**
     * Handles photo capture process.
     */
    private fun takePhoto() {
        cameraController.takePhoto(
            onSuccess = { uri -> _photoUri.value = uri },
            onError = { errorMessage -> setError(errorMessage) }
        )
    }
    /**
     * Handles the submission of the captured photo for AI-based meal estimation.
     * Also validates the meal data received from the backend.
     */
    private fun submitPhoto() {
        setLoading()
        viewModelScope.launch {
            val multipartBody = imageProcessor.convertUriToMultipartBody(_photoUri.value)
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
                if (isFoodDetected(meal)) {
                    historyRepository.addMeal(meal)
                    setReady()
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
     * Emits an event to the shared flow for UI consumption.
     * @param event The event to emit
     */
    private fun emitEvent(event: AddAiMealEvent) = viewModelScope.launch {
        _events.emit(event)
    }
    /**
     * Extension function to check if a meal contains detected food.
     * Food is considered detected if all nutritional values are greater than zero.
     * @return true if food is detected, false otherwise
     */
    private fun isFoodDetected(meal: Meal): Boolean {
        val firstFoodProduct = meal.mealFoodItems.firstOrNull()?.foodProduct
        return firstFoodProduct?.let { product ->
            product.calories > MIN_NUTRITIONAL_VALUE &&
                    product.carbohydrates > MIN_NUTRITIONAL_VALUE &&
                    product.protein > MIN_NUTRITIONAL_VALUE &&
                    product.fat > MIN_NUTRITIONAL_VALUE
        } == true
    }
}

/**
 * Hilt module that provides bindings for camera and image processing dependencies.
 *
 * This module binds the Android-specific implementations to their respective interfaces,
 * enabling dependency injection throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CameraModule {

    @Binds
    abstract fun bindImageProcessor(
        androidImageProcessor: AndroidImageProcessor
    ): ImageProcessor

    @Binds
    abstract fun bindCameraController(
        androidCameraController: AndroidCameraController
    ): CameraController
}