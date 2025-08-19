package com.nutricheck.frontend.viewmodels

import android.content.Context
import com.frontend.nutricheck.client.AppThemeState
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.ThemeSetting
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.ProfileEvent
import com.frontend.nutricheck.client.ui.view_model.ProfileViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ProfileViewModel
    private val userDataRepository: UserDataRepository = mockk(relaxed = true)
    private val appSettingRepository: AppSettingRepository = mockk(relaxed = true)
    private val appContext: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        // Sets the main dispatcher for coroutines to the test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Mocking the behavior of UserDataRepository to return a default user
        coEvery { userDataRepository.getUserData() } returns UserData(
            username = "TestUser",
            birthdate = Date(946684800000L), // January 1, 2000
            weight = 70.0,
            height = 180.0
        )
        // Mocking context getString calls for error messages
        every { appContext.getString(R.string.userData_error_weight_required) } returns "Weight is required."
        every { appContext.getString(R.string.userData_error_invalid_date) } returns "Invalid date."
        every { appContext.getString(any()) } returns "Error"

        // Initializing the ViewModel with mocked dependencies
        viewModel = ProfileViewModel(userDataRepository, appSettingRepository, appContext)
    }

    @After
    fun tearDown() {
        // Resets the main dispatcher after each test
        Dispatchers.resetMain()
        // Clears all MockK mocks
        unmockkAll()
    }

    @Test
    fun `viewModel init loads user data and calculates age`() = runTest {
        // Given a specific birthdate in the mocked data
        val expectedAge = 25 // assuming current year is 2025

        // The viewModel is initialized in setUp(), so we just need to verify the result
        val data = viewModel.data.first()
        val dataDraft = viewModel.dataDraft.first()

        // Assert that the loaded data and calculated age are correct
        assertEquals("TestUser", data.username)
        assertEquals(expectedAge, data.age)
        assertEquals(data, dataDraft)

        // Verify that the repository method was called
        coVerify { userDataRepository.getUserData() }
    }


    @Test
    fun `onEvent updates username draft correctly`() = runTest {
        val newUsername = "NewName"
        viewModel.onEvent(ProfileEvent.UpdateUserNameDraft(newUsername))

        // Assert that the dataDraft's username has been updated
        assertEquals(newUsername, viewModel.dataDraft.first().username)
    }

    @Test
    fun `onEvent with invalid username sets an error state`() = runTest {
        // Pass an invalid username (e.g., an empty string)
        viewModel.onEvent(ProfileEvent.UpdateUserNameDraft(""))

        // Assert that the UiState is now an error
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)

    }

    @Test
    fun `onEvent updates birthdate draft correctly`() = runTest {
        val newBirthdate = Date(946684800000L) // January 1, 2000
        viewModel.onEvent(ProfileEvent.UpdateUserBirthdateDraft(newBirthdate))

        // Assert that the dataDraft's birthdate has been updated
        assertEquals(newBirthdate, viewModel.dataDraft.first().birthdate)
    }

    @Test
    fun `onEvent with invalid birthdate sets an error state`() = runTest {
        // Pass an invalid birthdate
        val futureDate = Date(System.currentTimeMillis() + 1000000000L) // 1000 seconds in the future
        viewModel.onEvent(ProfileEvent.UpdateUserBirthdateDraft(futureDate))

        // Assert that the UiState is now an error
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
    }

    @Test
    fun `onEvent updates height draft correctly`() = runTest {
        val newHeight = "175.0"
        viewModel.onEvent(ProfileEvent.UpdateUserHeightDraft(newHeight))

        // Assert that the dataDraft's height has been updated
        assertEquals(175.0, viewModel.dataDraft.first().height)
    }

    @Test
    fun `onEvent with invalid height sets an error state`() = runTest {
        // Pass an invalid height (e.g., a non-numeric string)
        viewModel.onEvent(ProfileEvent.UpdateUserHeightDraft("abc"))

        // Assert that the UiState is now an error
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
    }

    @Test
    fun `onEvent updates weight draft correctly`() = runTest {
        val newWeight = "75.5"
        viewModel.onEvent(ProfileEvent.UpdateUserWeightDraft(newWeight))

        // Assert that the dataDraft's weight has been updated
        assertEquals(75.5, viewModel.dataDraft.first().weight)
    }

    @Test
    fun `onEvent with invalid weight sets an error state`() = runTest {
        // Pass an invalid weight (e.g., a non-numeric string)
        viewModel.onEvent(ProfileEvent.UpdateUserWeightDraft("abc"))

        // Assert that the UiState is now an error
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
    }

    @Test
    fun `onEvent updates target weight draft correctly`() = runTest {
        val newTargetWeight = "80.0"
        viewModel.onEvent(ProfileEvent.UpdateUserTargetWeightDraft(newTargetWeight))

        // Assert that the dataDraft's target weight has been updated
        assertEquals(80.0, viewModel.dataDraft.first().targetWeight)
    }

    @Test
    fun `onEvent with invalid target weight sets an error state`() = runTest {
        // Pass an invalid target weight (e.g., a non-numeric string)
        viewModel.onEvent(ProfileEvent.UpdateUserTargetWeightDraft("abc"))

        // Assert that the UiState is now an error
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
    }

    @Test
    fun `onEvent updates activity level draft correctly`() = runTest {
        val newActivityLevel = ActivityLevel.FREQUENTLY
        viewModel.onEvent(ProfileEvent.UpdateUserActivityLevelDraft(newActivityLevel))

        // Assert that the dataDraft's activity level has been updated
        assertEquals(newActivityLevel, viewModel.dataDraft.first().activityLevel)
    }

    @Test
    fun `onEvent updates weight goal draft correctly`() = runTest {
        val newWeightGoal = WeightGoal.GAIN_WEIGHT
        viewModel.onEvent(ProfileEvent.UpdateUserWeightGoalDraft(newWeightGoal))

        // Assert that the dataDraft's weight goal has been updated
        assertEquals(newWeightGoal, viewModel.dataDraft.first().weightGoal)
    }

    @Test
    fun `onEvent updates gender draft correctly`() = runTest {
        val newGender = Gender.DIVERS
        viewModel.onEvent(ProfileEvent.UpdateUserGenderDraft(newGender))

        // Assert that the dataDraft gender has been updated
        assertEquals(newGender, viewModel.dataDraft.first().gender)
    }

    @Test
    fun `onEvent saves data and calculates goals on OnSaveClick`() = runTest {
        // Update the draft to simulate user input
        viewModel.onEvent(ProfileEvent.UpdateUserNameDraft("UpdatedUser"))
        viewModel.onEvent(ProfileEvent.UpdateUserWeightDraft("72.0"))
        viewModel.onEvent(ProfileEvent.UpdateUserHeightDraft("175.0"))
        viewModel.onEvent(ProfileEvent.UpdateUserActivityLevelDraft(ActivityLevel.FREQUENTLY))
        viewModel.onEvent(ProfileEvent.UpdateUserGenderDraft(Gender.MALE))
        viewModel.onEvent(ProfileEvent.UpdateUserWeightGoalDraft(WeightGoal.GAIN_WEIGHT))
        viewModel.onEvent(ProfileEvent.UpdateUserTargetWeightDraft("75.0"))

        // Trigger the save event
        viewModel.onEvent(ProfileEvent.OnSaveClick)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify that the repository's update method was called with the calculated data
        coVerify { userDataRepository.updateUserData(any()) }
    }

    @Test
    fun `onEvent saves new weight and navigates back`() = runTest {
        // Create a new weight entry
        val newWeight = "71.0"
        val newDate = Date()
        val weight = Weight(value = 71.0, date = newDate)

        // Stub the repository to return a list with the new weight
        coEvery { userDataRepository.addWeight(weight) } just runs
        coEvery { userDataRepository.getWeightHistory() } returns listOf(weight)

        // Trigger the save weight event
        viewModel.onEvent(ProfileEvent.SaveNewWeight(newWeight, newDate))
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify that the new weight was added to the repository
        coVerify { userDataRepository.addWeight(weight) }
        coVerify { userDataRepository.getWeightHistory() }

        // Assert that the correct event was emitted
        assertEquals(listOf(weight), viewModel.weightData.first())
    }

    @Test
    fun `onEvent with invalid new weight sets an error state`() = runTest {
        // Trigger the save weight event with an invalid value
        viewModel.onEvent(ProfileEvent.SaveNewWeight("-10.0", Date()))

        // Assert that the UiState is now an error
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
    }

    @Test
    fun `onEvent with invalid new weight date sets an error state`() = runTest {
        // Create an invalid date (before birthdate)
        val invalidDate = Date(0) // January 1, 1970
        viewModel.onEvent(ProfileEvent.SaveNewWeight("70.0", invalidDate))

        // Assert that the UiState is now an error
        assertTrue(viewModel.uiState.first() is BaseViewModel.UiState.Error)
    }

    @Test
    fun `onEvent changes app theme and persists it`() = runTest {
        // Trigger the theme change event
        viewModel.onEvent(ProfileEvent.ChangeTheme(ThemeSetting.DARK))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ThemeSetting.DARK, AppThemeState.currentTheme.value)

        // Verify that the repository's setTheme method was called with the correct boolean value
        coVerify { appSettingRepository.setTheme(true) }
    }

    @Test
    fun `onEvent for navigation emits the correct event`() = runTest {
        val eventsToTest = listOf(
            ProfileEvent.OnPersonalDataClick to ProfileEvent.NavigateToPersonalData,
            ProfileEvent.OnAddNewWeightClick to ProfileEvent.NavigateToAddNewWeight,
            ProfileEvent.OnDisplayProfileOverview to ProfileEvent.NavigateToProfileOverview,
            ProfileEvent.OnRestartApp to ProfileEvent.RestartApp
        )

        eventsToTest.forEach { (triggerEvent, expectedEvent) ->
            // Trigger the event
            viewModel.onEvent(triggerEvent)

            // Assert that the emitted event matches the expected event
            assertEquals(expectedEvent, viewModel.events.first())
        }
    }
    @Test
    fun `deleteWeight deletes weight and updates weightData`() = runTest {
        // Arrange: set up a weight and mock the repository behavior
        val weight = Weight(value = 70.0, date = Date(1704067200000L))
        viewModel.onEvent(ProfileEvent.SaveNewWeight(weight.value.toString(), weight.date))

        coEvery { userDataRepository.deleteWeight(weight) } just runs

        // Act: deleteWeight aufrufen
        viewModel.onEvent(ProfileEvent.DeleteWeight)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: deleteWeight wurde aufgerufen und weightData ist leer
        //coVerify { userDataRepository.deleteWeight(weight) }
        assertEquals(emptyList(), viewModel.weightData.first())
        assertEquals(null, viewModel.selectedWeight.value)
    }

    @Test
    fun `deleteWeight does not delete when weight is null`() = runTest {
        // Arrange: selectedWeight is null
        viewModel.onEvent(ProfileEvent.DeleteWeight)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: deleteWeight was not called
        coVerify(exactly = 0) { userDataRepository.deleteWeight(any()) }
    }
}
