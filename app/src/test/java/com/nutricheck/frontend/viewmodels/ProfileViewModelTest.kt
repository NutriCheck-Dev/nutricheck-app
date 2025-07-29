package com.nutricheck.frontend.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.Utils
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileEvent
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date


@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var userDataRepository: UserDataRepository

    @Mock
    private lateinit var appSettingsRepository: AppSettingsRepository

    private lateinit var profileViewModel: ProfileViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }
    @Test
    fun `init loads user data from repository and updates data and dataDraft`() = runTest {
        val birthdate = Date(1234567890L)
        val userData = UserData(
            username = "TestUser",
            birthdate = birthdate,
            gender = Gender.MALE,
            height = 175.0,
            weight = 70.0,
            targetWeight = 65.0,
            activityLevel = ActivityLevel.REGULARLY,
            weightGoal = WeightGoal.LOSE_WEIGHT,
            age = 30,
            dailyCaloriesGoal = 2000,
            proteinGoal = 100,
            carbsGoal = 250,
            fatsGoal = 70
        )
        whenever(userDataRepository.getUserData()).thenReturn(userData)
        // When
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        // Wait for the ViewModel to initialize and load data
        profileViewModel.data.first() { it == userData }
        profileViewModel.dataDraft.first() { it == userData }
        // Then
        assertThat(profileViewModel.data.value).isEqualTo(userData)
        assertThat(profileViewModel.dataDraft.value).isEqualTo(userData)
        verify(userDataRepository).getUserData()
    }
    @Test
    fun `updateUserNameDraft with valid name updates draft`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 0.0,
                weight = 0.0,
                targetWeight = 0.0,
                activityLevel = ActivityLevel.NEVER,
                weightGoal = WeightGoal.LOSE_WEIGHT,
                age = 0,
                dailyCaloriesGoal = 0,
                proteinGoal = 0,
                carbsGoal = 0,
                fatsGoal = 0
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)

        // When
        profileViewModel.onEvent(ProfileEvent.UpdateUserNameDraft("TestUser"))

        // Then
        assertThat(profileViewModel.errorMessage.value).isNull()
        assertThat(profileViewModel.dataDraft.value.username).isEqualTo("TestUser")
    }
    @Test
    fun `updateUserNameDraft with blank name sets error message`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 0.0,
                weight = 0.0,
                targetWeight = 0.0,
                activityLevel = ActivityLevel.NEVER,
                weightGoal = WeightGoal.LOSE_WEIGHT,
                age = 0,
                dailyCaloriesGoal = 0,
                proteinGoal = 0,
                carbsGoal = 0,
                fatsGoal = 0
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        // When
        profileViewModel.onEvent(ProfileEvent.UpdateUserNameDraft(""))

        // Then
        assertThat(profileViewModel.errorMessage.value)
            .isEqualTo(R.string.userData_error_name_required)
        assertThat(profileViewModel.dataDraft.value.username).isEmpty()
    }

    @Test
    fun `updateUserDataDraft with valid birthdate updates draft`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "TestUser",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 0.0,
                weight = 0.0,
                targetWeight = 0.0,
                activityLevel = ActivityLevel.NEVER,
                weightGoal = WeightGoal.LOSE_WEIGHT,
                age = 0,
                dailyCaloriesGoal = 0,
                proteinGoal = 0,
                carbsGoal = 0,
                fatsGoal = 0
            ))
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)

        // When
        val newBirthdate = Date(946684800000L) // 2000-01-01
        profileViewModel.onEvent(ProfileEvent.UpdateUserBirthdateDraft(birthdate = newBirthdate))

        // Then
        assertThat(profileViewModel.errorMessage.value).isNull()
        assertThat(profileViewModel.dataDraft.value.birthdate).isEqualTo(newBirthdate)

    }

    @Test
    fun `updateUserDataDraft with invalid birthdate sets error message`() = runTest {
        val initialBirthdate  = Date(1234567890L) // Valid date
        val initialUserData = UserData(
            username = "TestUser",
            birthdate = initialBirthdate,
            gender = Gender.MALE,
            height = 0.0,
            weight = 0.0,
            targetWeight = 0.0,
            activityLevel = ActivityLevel.NEVER,
            weightGoal = WeightGoal.LOSE_WEIGHT,
            age = 0,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )
        whenever(userDataRepository.getUserData()).thenReturn(initialUserData)
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        // Ensure initial data is loaded
        profileViewModel.data.first { it == initialUserData }
        profileViewModel.dataDraft.first { it == initialUserData }

        // When
        val invalidBirthdate = Date(-4417632000000L) // Invalid date 1830-01-01
        profileViewModel.onEvent(ProfileEvent.
        UpdateUserBirthdateDraft(birthdate = invalidBirthdate))

        // Then
        assertThat(profileViewModel.errorMessage.value)
            .isEqualTo(R.string.userData_error_birthdate_required)
        assertThat(profileViewModel.dataDraft.value.birthdate).isEqualTo(initialBirthdate)
    }
    



    class MainCoroutineRule : TestWatcher() {
        override fun starting(description: Description?) {
            Dispatchers.setMain(StandardTestDispatcher())
        }
        override fun finished(description: Description?) {
            Dispatchers.resetMain()
        }
    }
}