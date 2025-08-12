package com.nutricheck.frontend.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealEvent
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for AddAiMealViewModel
 *
 * Tests cover:
 * - Event handling
 * - Photo capture state management
 * - AI meal submission and validation
 * - Error handling scenarios
 * - Photo retake functionality
 */
@ExperimentalCoroutinesApi
class AddAiMealViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK private lateinit var historyRepository: HistoryRepository
    @MockK private lateinit var appContext: Context
    @MockK private lateinit var contentResolver: ContentResolver
    @MockK private lateinit var mockUri: Uri
    @MockK private lateinit var multipartBody: MultipartBody.Part

    private lateinit var viewModel: AddAiMealViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Mock context and content resolver
        every { appContext.contentResolver } returns contentResolver
        every { appContext.getString(R.string.error_no_photo_taken) } returns "No photo taken error"
        every { appContext.getString(R.string.error_encoding_image) } returns "Image encoding error"
        every { appContext.getString(R.string.error_no_food_detected) } returns "No food detected error"

        viewModel = AddAiMealViewModel(historyRepository, appContext)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        // Given - initial state

        // When - checking initial values
        val initialSurfaceRequest = viewModel.surfaceRequest.first()
        val initialPhotoUri = viewModel.photoUri.first()

        // Then - should be null initially
        assertNull(initialSurfaceRequest)
        assertNull(initialPhotoUri)
    }

    @Test
    fun `onEvent with OnRetakePhoto should reset photo URI`() = runTest {
        // Given - viewModel with photo URI set
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        // Simulate photo taken by setting URI manually for test
        val privatePhotoUri = viewModel::class.java.getDeclaredField("_photoUri")
        privatePhotoUri.isAccessible = true
        val photoUriStateFlow = privatePhotoUri.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<Uri?>
        photoUriStateFlow.value = mockUri

        // When - retaking photo
        viewModel.onEvent(AddAiMealEvent.OnRetakePhoto)

        // Then - photo URI should be reset to null
        assertNull(viewModel.photoUri.value)
    }

    @Test
    fun `onEvent with ResetErrorState should set state to ready`() = runTest {
        // Given - viewModel in error state
        val setErrorMethod = viewModel::class.java.getDeclaredMethod("setError", String::class.java)
        setErrorMethod.isAccessible = true
        setErrorMethod.invoke(viewModel, "Test error")

        // When - resetting error state
        viewModel.onEvent(AddAiMealEvent.ResetErrorState)
        assertTrue(viewModel.uiState.value is BaseViewModel.UiState.Ready)
    }

    @Test
    fun `onEvent with OnSubmitPhoto should handle successful meal detection`() = runTest {
        // Given - mock successful response
        val foodProduct = createMockFoodProduct(
            calories = 100.0,
            carbohydrates = 20.0,
            protein = 15.0,
            fat = 5.0
        )
        val mealFoodItem = createMockMealFoodItem(foodProduct = foodProduct)
        val meal = createMockMeal(mealFoodItems = listOf(mealFoodItem))

        coEvery { historyRepository.requestAiMeal(any()) } returns Result.Success(meal)
        coEvery { historyRepository.addMeal(meal) } returns Unit

        // Mock URI to multipart conversion (this would require extensive mocking of Android APIs)
        mockkStatic("android.graphics.BitmapFactory")
        mockkStatic("androidx.exifinterface.media.ExifInterface")

        // Set photo URI
        setPhotoUriPrivately(mockUri)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should add meal and emit show meal overview event
        coVerify { historyRepository.addMeal(meal) }
    }

    @Test
    fun `onEvent with OnSubmitPhoto should handle no food detected`() = runTest {
        // Given - mock response with no nutritional values (no food detected)
        val foodProduct = createMockFoodProduct(
            calories = 0.0,
            carbohydrates = 0.0,
            protein = 0.0,
            fat = 0.0
        )
        val mealFoodItem = createMockMealFoodItem(foodProduct = foodProduct)
        val meal = createMockMeal(mealFoodItems = listOf(mealFoodItem))

        coEvery { historyRepository.requestAiMeal(any()) } returns Result.Success(meal)

        // Set photo URI
        setPhotoUriPrivately(mockUri)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should not add meal and should reset photo URI
        coVerify(exactly = 0) { historyRepository.addMeal(any()) }
        assertNull(viewModel.photoUri.value)
    }

    @Test
    fun `onEvent with OnSubmitPhoto should handle API error`() = runTest {
        // Given - mock error response
        coEvery { historyRepository.requestAiMeal(any()) } returns Result.Error(500, "Server error")

        // Set photo URI
        setPhotoUriPrivately(mockUri)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should not add meal and should reset photo URI
        coVerify(exactly = 0) { historyRepository.addMeal(any()) }
        assertNull(viewModel.photoUri.value)
    }

    @Test
    fun `onEvent with OnSubmitPhoto should handle null photo URI`() = runTest {
        // Given - no photo URI set (remains null)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should not call repository
        coVerify(exactly = 0) { historyRepository.requestAiMeal(any()) }
    }

    @Test
    fun `meal with valid nutritional values should be detected as food`() = runTest {
        // Given - meal with valid nutritional values
        val foodProduct = createMockFoodProduct(
            calories = 250.0,
            carbohydrates = 30.0,
            protein = 20.0,
            fat = 10.0
        )
        val mealFoodItem = createMockMealFoodItem(foodProduct = foodProduct)
        val meal = createMockMeal(mealFoodItems = listOf(mealFoodItem))

        coEvery { historyRepository.requestAiMeal(any()) } returns Result.Success(meal)
        coEvery { historyRepository.addMeal(meal) } returns Unit

        setPhotoUriPrivately(mockUri)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should add meal
        coVerify { historyRepository.addMeal(meal) }
    }

    @Test
    fun `meal with zero calories should not be detected as food`() = runTest {
        // Given - meal with zero calories
        val foodProduct = createMockFoodProduct(
            calories = 0.0,
            carbohydrates = 30.0,
            protein = 20.0,
            fat = 10.0
        )
        val mealFoodItem = createMockMealFoodItem(foodProduct = foodProduct)
        val meal = createMockMeal(mealFoodItems = listOf(mealFoodItem))

        coEvery { historyRepository.requestAiMeal(any()) } returns Result.Success(meal)

        setPhotoUriPrivately(mockUri)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should not add meal
        coVerify(exactly = 0) { historyRepository.addMeal(any()) }
    }

    @Test
    fun `meal with zero protein should not be detected as food`() = runTest {
        // Given - meal with zero protein
        val foodProduct = createMockFoodProduct(
            calories = 250.0,
            carbohydrates = 30.0,
            protein = 0.0,
            fat = 10.0
        )
        val mealFoodItem = createMockMealFoodItem(foodProduct = foodProduct)
        val meal = createMockMeal(mealFoodItems = listOf(mealFoodItem))

        coEvery { historyRepository.requestAiMeal(any()) } returns Result.Success(meal)

        setPhotoUriPrivately(mockUri)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should not add meal
        coVerify(exactly = 0) { historyRepository.addMeal(any()) }
    }

    @Test
    fun `meal with empty food items should not be detected as food`() = runTest {
        // Given - meal with empty food items list
        val meal = createMockMeal(mealFoodItems = emptyList())

        coEvery { historyRepository.requestAiMeal(any()) } returns Result.Success(meal)

        setPhotoUriPrivately(mockUri)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should not add meal
        coVerify(exactly = 0) { historyRepository.addMeal(any()) }
    }

    @Test
    fun `successful meal detection should emit ShowMealOverview event`() = runTest {
        // Given - mock successful response
        val foodProduct = createMockFoodProduct(
            id = "food123",
            calories = 100.0,
            carbohydrates = 20.0,
            protein = 15.0,
            fat = 5.0
        )
        val mealFoodItem = createMockMealFoodItem(foodProduct = foodProduct)
        val meal = createMockMeal(id = "meal123", mealFoodItems = listOf(mealFoodItem))

        coEvery { historyRepository.requestAiMeal(any()) } returns Result.Success(meal)
        coEvery { historyRepository.addMeal(meal) } returns Unit

        setPhotoUriPrivately(mockUri)

        val eventDeferred = async { viewModel.events.first() }

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should emit ShowMealOverview event
        val event = eventDeferred.await()
        assertTrue(event is AddAiMealEvent.ShowMealOverview)
        val showEvent = event
        assertEquals("meal123", showEvent.mealId)
        assertEquals("food123", showEvent.foodProductId)
    }

    private fun createMockFoodProduct(
        id: String = "test_food_id",
        name: String = "Test Food",
        calories: Double = 0.0,
        carbohydrates: Double = 0.0,
        protein: Double = 0.0,
        fat: Double = 0.0,
        servings: Int = 1,
        servingSize: ServingSize = ServingSize.ONEHOUNDREDGRAMS
    ): FoodProduct {
        return FoodProduct(
            id = id,
            name = name,
            calories = calories,
            carbohydrates = carbohydrates,
            protein = protein,
            fat = fat,
            servings = servings,
            servingSize = servingSize
        )
    }

    private fun createMockMealFoodItem(
        mealId: String = "test_meal_id",
        foodProduct: FoodProduct = createMockFoodProduct(),
        quantity: Double = 100.0,
        servings: Int = 1,
        servingSize: ServingSize = ServingSize.ONEHOUNDREDGRAMS
    ): MealFoodItem {
        return MealFoodItem(
            mealId = mealId,
            foodProduct = foodProduct,
            quantity = quantity,
            servings = servings,
            servingSize = servingSize
        )
    }

    private fun createMockMeal(
        id: String = "test_meal_id",
        calories: Double = 100.0,
        carbohydrates: Double = 20.0,
        protein: Double = 15.0,
        fat: Double = 5.0,
        date: Date = Date(),
        dayTime: DayTime = DayTime.LUNCH,
        mealFoodItems: List<MealFoodItem> = emptyList(),
        mealRecipeItems: List<MealRecipeItem> = emptyList()
    ): Meal {
        return Meal(
            id = id,
            calories = calories,
            carbohydrates = carbohydrates,
            protein = protein,
            fat = fat,
            date = date,
            dayTime = dayTime,
            mealFoodItems = mealFoodItems,
            mealRecipeItems = mealRecipeItems
        )
    }

    /**
     * Helper method to set photo URI privately using reflection
     * This is needed because _photoUri is private in the ViewModel
     */
    private fun setPhotoUriPrivately(uri: Uri) {
        val privatePhotoUri = viewModel::class.java.getDeclaredField("_photoUri")
        privatePhotoUri.isAccessible = true
        val photoUriStateFlow = privatePhotoUri.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<Uri?>
        photoUriStateFlow.value = uri
    }

    // Additional tests for edge cases

    @Test
    fun `onEvent with unknown event should not crash`() {
        // Given - custom event that's not handled in when statement
        val customEvent = AddAiMealEvent.ShowMealOverview("meal123", "food123")

        // When - handling unknown event
        viewModel.onEvent(customEvent)

        // Then - should not crash (no assertion needed, test passes if no exception)
    }

    @Test
    fun `multiple retake photo calls should not cause issues`() = runTest {
        // Given - photo URI set
        setPhotoUriPrivately(mockUri)
        assertNotNull(viewModel.photoUri.value)

        // When - calling retake photo multiple times
        viewModel.onEvent(AddAiMealEvent.OnRetakePhoto)
        viewModel.onEvent(AddAiMealEvent.OnRetakePhoto)
        viewModel.onEvent(AddAiMealEvent.OnRetakePhoto)

        // Then - should remain null
        assertNull(viewModel.photoUri.value)
    }

    @Test
    fun `submit photo without setting URI first should handle gracefully`() = runTest {
        // Given - no photo URI set
        assertNull(viewModel.photoUri.value)

        // When - submitting photo
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - should not make API call
        coVerify(exactly = 0) { historyRepository.requestAiMeal(any()) }
    }
}

