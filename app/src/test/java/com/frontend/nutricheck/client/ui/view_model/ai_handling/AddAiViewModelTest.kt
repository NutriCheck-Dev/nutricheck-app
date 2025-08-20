package com.frontend.nutricheck.client.ui.view_model.ai_handling

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.Language
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit test class for [AddAiMealViewModel] using MockK framework.
 *
 * Tests the AI meal estimation functionality including camera operations,
 * image processing, API communication, and meal validation.
 */
@ExperimentalCoroutinesApi
class AddAiMealViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AddAiMealViewModel
    private val application: Application = mockk(relaxed = true)
    private val appContext: Context = mockk(relaxed = true)
    private val appSettingRepository: AppSettingRepository = mockk(relaxed = true)
    private val historyRepository: HistoryRepository = mockk(relaxed = true)
    private val imageProcessor: AndroidImageProcessor = mockk(relaxed = true)
    private val cameraController: CameraController = mockk(relaxed = true)
    private val lifecycleOwner: LifecycleOwner = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock application context
        every { application.applicationContext } returns appContext

        // Mock string resources
        every { appContext.getString(R.string.error_encoding_image) } returns "Error encoding image"
        every { appContext.getString(R.string.error_no_food_detected) } returns "No food detected"
        every { appContext.getString(R.string.error_ai_server_response) } returns "Server error"

        // Mock app setting repository for language
        val mockLanguage = mockk<Language> {
            every { code } returns "en"
        }
        every { appSettingRepository.language } returns flowOf(mockLanguage)

        // Initialize ViewModel
        viewModel = AddAiMealViewModel(
            application = application,
            appSettingRepository = appSettingRepository,
            historyRepository = historyRepository,
            imageProcessor = imageProcessor,
            cameraController = cameraController
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    /**
     * Tests that the ViewModel initializes with correct default states.
     */
    @Test
    fun `viewModel initializes with correct default states`() = runTest {
        // Assert initial states
        assertNull(viewModel.photoUri.first())
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Ready)
    }

    /**
     * Tests successful photo capture updates photoUri state.
     */
    @Test
    fun `onEvent OnTakePhoto with successful capture updates photoUri`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        every {
            cameraController.takePhoto(any(), any())
        } answers {
            firstArg<(Uri?) -> Unit>().invoke(mockUri)
        }

        // When
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(mockUri, viewModel.photoUri.first())
        verify { cameraController.takePhoto(any(), any()) }
    }

    /**
     * Tests failed photo capture sets error state.
     */
    @Test
    fun `onEvent OnTakePhoto with failed capture sets error state`() = runTest {
        // Given
        val errorMessage = "Camera error"
        every {
            cameraController.takePhoto(any(), any())
        } answers {
            secondArg<(String) -> Unit>().invoke(errorMessage)
        }

        // When
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
        verify { cameraController.takePhoto(any(), any()) }
    }

    /**
     * Tests retake photo resets photoUri to null.
     */
    @Test
    fun `onEvent OnRetakePhoto resets photoUri to null`() = runTest {
        // Given - set initial photo URI
        val mockUri = mockk<Uri>()
        every {
            cameraController.takePhoto(any(), any())
        } answers {
            firstArg<(Uri?) -> Unit>().invoke(mockUri)
        }
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(AddAiMealEvent.OnRetakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertNull(viewModel.photoUri.first())
    }

    /**
     * Tests successful photo submission with valid meal data.
     */
    @Test
    fun `onEvent OnSubmitPhoto with successful AI response and valid food adds meal`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val mockMultipartBody = mockk<MultipartBody.Part>()
        val validMeal = createValidMeal()
        val languageCode = "en"

        // Set up photo URI
        every {
            cameraController.takePhoto(any(), any())
        } answers {
            firstArg<(Uri?) -> Unit>().invoke(mockUri)
        }
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Mock image processing
        every { imageProcessor.convertUriToMultipartBody(mockUri) } returns mockMultipartBody

        // Mock API response
        coEvery { historyRepository.requestAiMeal(mockMultipartBody, languageCode) } returns
                Result.Success(validMeal)
        coEvery { historyRepository.addMeal(validMeal) } just runs

        // When
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Ready)
        coVerify { historyRepository.requestAiMeal(mockMultipartBody, languageCode) }
        coVerify { historyRepository.addMeal(validMeal) }
    }

    /**
     * Tests photo submission with invalid meal data (no food detected).
     */
    @Test
    fun `onEvent OnSubmitPhoto with invalid meal data sets error state`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val mockMultipartBody = mockk<MultipartBody.Part>()
        val invalidMeal = createInvalidMeal() // All nutritional values are 0
        val languageCode = "en"

        // Set up photo URI
        every {
            cameraController.takePhoto(any(), any())
        } answers {
            firstArg<(Uri?) -> Unit>().invoke(mockUri)
        }
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Mock image processing
        every { imageProcessor.convertUriToMultipartBody(mockUri) } returns mockMultipartBody

        // Mock API response with invalid meal
        coEvery { historyRepository.requestAiMeal(mockMultipartBody, languageCode) } returns
                Result.Success(invalidMeal)

        // When
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
        assertNull(viewModel.photoUri.first()) // Should reset photoUri on error
        coVerify { historyRepository.requestAiMeal(mockMultipartBody, languageCode) }
        coVerify(exactly = 0) { historyRepository.addMeal(any()) }
    }

    /**
     * Tests photo submission with API error response.
     */
    @Test
    fun `onEvent OnSubmitPhoto with API error sets error state`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val mockMultipartBody = mockk<MultipartBody.Part>()
        val languageCode = "en"

        // Set up photo URI
        every {
            cameraController.takePhoto(any(), any())
        } answers {
            firstArg<(Uri?) -> Unit>().invoke(mockUri)
        }
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Mock image processing
        every { imageProcessor.convertUriToMultipartBody(mockUri) } returns mockMultipartBody

        // Mock API error response
        coEvery { historyRepository.requestAiMeal(mockMultipartBody, languageCode) } returns
                Result.Error(500, "Server error")

        // When
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
        assertNull(viewModel.photoUri.first()) // Should reset photoUri on error
        coVerify { historyRepository.requestAiMeal(mockMultipartBody, languageCode) }
        coVerify(exactly = 0) { historyRepository.addMeal(any()) }
    }

    /**
     * Tests photo submission with null multipart body sets error state.
     */
    @Test
    fun `onEvent OnSubmitPhoto with null multipart body sets error state`() = runTest {
        // Given
        val mockUri = mockk<Uri>()

        // Set up photo URI
        every {
            cameraController.takePhoto(any(), any())
        } answers {
            firstArg<(Uri?) -> Unit>().invoke(mockUri)
        }
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Mock image processing to return null
        every { imageProcessor.convertUriToMultipartBody(mockUri) } returns null

        // When
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
        assertNull(viewModel.photoUri.first()) // Should reset photoUri on error
        verify { imageProcessor.convertUriToMultipartBody(mockUri) }
        coVerify(exactly = 0) { historyRepository.requestAiMeal(any(), any()) }
    }

    /**
     * Tests photo submission without captured photo (null photoUri) doesn't crash.
     */
    @Test
    fun `onEvent OnSubmitPhoto with null photoUri sets error state`() = runTest {
        // Given - no photo captured (photoUri is null by default)

        // Mock image processing to return null for null URI
        every { imageProcessor.convertUriToMultipartBody(null) } returns null

        // When
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
        verify { imageProcessor.convertUriToMultipartBody(null) }
        coVerify(exactly = 0) { historyRepository.requestAiMeal(any(), any()) }
    }

    /**
     * Tests reset error state event returns to ready state.
     */
    @Test
    fun `onEvent ResetErrorState sets state to ready`() = runTest {
        // Given - set error state first
        every {
            cameraController.takePhoto(any(), any())
        } answers {
            secondArg<(String) -> Unit>().invoke("Error")
        }
        viewModel.onEvent(AddAiMealEvent.OnTakePhoto)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(AddAiMealEvent.ResetErrorState)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Ready)
    }

    /**
     * Tests binding camera to lifecycle owner.
     */
    @Test
    fun `bindToCamera delegates to camera controller`() = runTest {
        // Given
        val context = mockk<Context>()
        coEvery { cameraController.bindToCamera(context, lifecycleOwner) } just runs

        // When
        viewModel.bindToCamera(context, lifecycleOwner)

        // Then
        coVerify { cameraController.bindToCamera(context, lifecycleOwner) }
    }

    /**
     * Creates a valid meal object with proper nutritional values for testing.
     */
    private fun createValidMeal(): Meal {
        val foodProduct = FoodProduct(
            id = "food1",
            name = "Test Food",
            calories = 100.0,
            carbohydrates = 25.0,
            protein = 10.0,
            fat = 5.0,
            servings = 1
        )

        val mealFoodItem = MealFoodItem(
            mealId = "meal1",
            foodProduct = foodProduct,
            quantity = 100.0,
            servings = 1
        )

        return Meal(
            id = "meal1",
            calories = 100.0,
            carbohydrates = 25.0,
            protein = 10.0,
            fat = 5.0,
            date = Date(),
            dayTime = DayTime.LUNCH,
            mealFoodItems = listOf(mealFoodItem),
            mealRecipeItems = emptyList()
        )
    }

    /**
     * Creates an invalid meal object with zero nutritional values for testing.
     */
    private fun createInvalidMeal(): Meal {
        val foodProduct = FoodProduct(
            id = "food1",
            name = "Test Food",
            calories = 0.0,
            carbohydrates = 0.0,
            protein = 0.0,
            fat = 0.0,
            servings = 1
        )

        val mealFoodItem = MealFoodItem(
            mealId = "meal1",
            foodProduct = foodProduct,
            quantity = 0.0,
            servings = 1
        )

        return Meal(
            id = "meal1",
            calories = 0.0,
            carbohydrates = 0.0,
            protein = 0.0,
            fat = 0.0,
            date = Date(),
            dayTime = DayTime.LUNCH,
            mealFoodItems = listOf(mealFoodItem),
            mealRecipeItems = emptyList()
        )
    }
}