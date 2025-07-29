package com.nutricheck.frontend.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frontend.nutricheck.client.AppThemeState
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.Language
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileEvent
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileViewModel
import com.frontend.nutricheck.client.model.data_sources.data.ThemeSetting
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
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

    class MainCoroutineRule : TestWatcher() {
        override fun starting(description: Description?) {
            Dispatchers.setMain(StandardTestDispatcher())
        }
        override fun finished(description: Description?) {
            Dispatchers.resetMain()
        }
    }
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
        val birthdate = Date(1234567890L) // 1970-01-01
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
    @Test
    fun `updateUserHeightDraft updates valid height`() = runTest {
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
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        profileViewModel.data.first { it.height == 0.0 }
        profileViewModel.dataDraft.first { it.height == 0.0 }

        profileViewModel.onEvent(ProfileEvent.UpdateUserHeightDraft("180"))

        assertThat(profileViewModel.errorMessage.value).isNull()
        assertThat(profileViewModel.dataDraft.value.height).isEqualTo(180.0)
    }
    @Test
    fun `updateUserHeightDraft with invalid height sets error message`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "TestUser",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 180.0,
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
        profileViewModel.data.first { it.height == 180.0 }
        profileViewModel.dataDraft.first { it.height == 180.0 }

        profileViewModel.onEvent(ProfileEvent.UpdateUserHeightDraft("-10"))

        assertThat(profileViewModel.errorMessage.value)
            .isEqualTo(R.string.userData_error_height_required)
        assertThat(profileViewModel.dataDraft.value.height).isEqualTo(180.0)
    }

    @Test
    fun `updateUserWeightDraft updated valid weight`() = runTest {
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
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        profileViewModel.data.first { it.weight == 0.0 }
        profileViewModel.dataDraft.first { it.weight == 0.0 }

        profileViewModel.onEvent(ProfileEvent.UpdateUserWeightDraft("75"))

        assertThat(profileViewModel.errorMessage.value).isNull()
        assertThat(profileViewModel.dataDraft.value.weight).isEqualTo(75.0)
    }

    @Test
    fun `updateUserWeightDraft with invalid weight sets error message`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "TestUser",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 0.0,
                weight = 80.0,
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
        profileViewModel.data.first { it.weight == 80.0 }
        profileViewModel.dataDraft.first { it.weight == 80.0 }

        profileViewModel.onEvent(ProfileEvent.UpdateUserWeightDraft("-10")) // Invalid weight

        assertThat(profileViewModel.errorMessage.value)
            .isEqualTo(R.string.userData_error_weight_required)
        assertThat(profileViewModel.dataDraft.value.weight).isEqualTo(80.0)
    }
    @Test
    fun `updateUserTargetWeightDraft updates with valid targetWeight`() = runTest {
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
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        profileViewModel.data.first { it.targetWeight == 0.0 }
        profileViewModel.dataDraft.first { it.targetWeight == 0.0 }

        profileViewModel.onEvent(ProfileEvent.UpdateUserTargetWeightDraft("70"))

        assertThat(profileViewModel.errorMessage.value).isNull()
        assertThat(profileViewModel.dataDraft.value.targetWeight).isEqualTo(70.0)
    }
    @Test
    fun `updateUserTargetWeightDraft with invalid targetWeight sets error message`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "TestUser",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 0.0,
                weight = 0.0,
                targetWeight = 80.0,
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
        profileViewModel.data.first { it.targetWeight == 80.0 }
        profileViewModel.dataDraft.first { it.targetWeight == 80.0 }

        profileViewModel.onEvent(ProfileEvent.UpdateUserTargetWeightDraft("-5"))

        assertThat(profileViewModel.errorMessage.value)
            .isEqualTo(R.string.userData_error_target_weight_required)
        assertThat(profileViewModel.dataDraft.value.targetWeight).isEqualTo(80.0)
    }

    @Test
    fun `updateUserActivityLevelDraft updates activitylevel`() = runTest {
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
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)

        profileViewModel.onEvent(ProfileEvent.UpdateUserActivityLevelDraft(ActivityLevel.REGULARLY))

        assertThat(profileViewModel.dataDraft.value.activityLevel).isEqualTo(ActivityLevel.REGULARLY)
    }

    @Test
    fun `updateUserWeightGoalDraft updates weightGoal`() = runTest {
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
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)

        profileViewModel.onEvent(ProfileEvent.UpdateUserWeightGoalDraft(WeightGoal.MAINTAIN_WEIGHT))

        assertThat(profileViewModel.dataDraft.value.weightGoal).isEqualTo(WeightGoal.MAINTAIN_WEIGHT)
    }
    @Test
    fun `updateUserGenderDraft updates gender`() = runTest {
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
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)

        profileViewModel.onEvent(ProfileEvent.UpdateUserGenderDraft(Gender.FEMALE))

        assertThat(profileViewModel.dataDraft.value.gender).isEqualTo(Gender.FEMALE)
    }
    @Test
    fun `persistDataWithCalculation persists Data`() = runTest {
        val userData = UserData(
            username = "TestUser",
            birthdate = Date(1234567890L),
            gender = Gender.MALE,
            height = 180.0,
            weight = 80.0,
            targetWeight = 75.0,
            activityLevel = ActivityLevel.REGULARLY,
            weightGoal = WeightGoal.LOSE_WEIGHT,
            age = 55,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )
        whenever(userDataRepository.getUserData()).thenReturn(userData)
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        profileViewModel.dataDraft.first { it == userData }
        profileViewModel.onEvent(ProfileEvent.UpdateUserNameDraft("NewTestUser"))
        profileViewModel.onEvent(ProfileEvent.UpdateUserHeightDraft("185"))
        profileViewModel.onEvent(ProfileEvent.UpdateUserBirthdateDraft(Date(68169600000L)))
        profileViewModel.onEvent(ProfileEvent.UpdateUserActivityLevelDraft(ActivityLevel.FREQUENTLY))

        profileViewModel.onEvent(ProfileEvent.OnSaveClick)
        advanceUntilIdle()

        verify(userDataRepository).updateUserData(org.mockito.kotlin.check {
            assertThat(it.username).isEqualTo("NewTestUser")
            assertThat(it.height).isEqualTo(185.0)
            assertThat(it.birthdate).isEqualTo(Date(68169600000L))
            assertThat(it.activityLevel).isEqualTo(ActivityLevel.FREQUENTLY)
            assertThat(it.age).isEqualTo(53) // 2025 - 1972
            assertThat(it.dailyCaloriesGoal).isEqualTo(3232)
        })
    }
    @Test
    fun `saveNewWeight with valid weight`() = runTest {
        val birthdate = Date(1234567890L)
        val userData = UserData(
            username = "TestUser",
                birthdate = birthdate,
                gender = Gender.MALE,
                height = 0.0,
                weight = 0.0,
                targetWeight = 0.0,
                activityLevel = ActivityLevel.NEVER,
                weightGoal = WeightGoal.LOSE_WEIGHT,
                age = 55,
                dailyCaloriesGoal = 0,
                proteinGoal = 0,
                carbsGoal = 0,
                fatsGoal = 0
        )
        whenever(userDataRepository.getUserData()).thenReturn(userData)
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        profileViewModel.data.first { it == userData }
        profileViewModel.dataDraft.first { it == userData }

        val date = Date(157783600000L) // 2020-01-01
        profileViewModel.onEvent(ProfileEvent.SaveNewWeight("80", date))
        advanceUntilIdle()

        verify(userDataRepository).addWeight(org.mockito.kotlin.check {
            assertThat(it.value).isEqualTo(80.0)
            assertThat(it.enterDate).isEqualTo(date)
        })
    }
    @Test
    fun `saveNewWeight with invalid weight sets error message`() = runTest {
        val birthdate = Date(946684800000L) // 2000-01-01
        val userData = UserData(
            username = "TestUser",
            birthdate = birthdate,
            gender = Gender.MALE,
            height = 180.0,
            weight = 80.0,
            targetWeight = 75.0,
            activityLevel = ActivityLevel.REGULARLY,
            weightGoal = WeightGoal.LOSE_WEIGHT,
            age = 25,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )
        whenever(userDataRepository.getUserData()).thenReturn(userData)
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        profileViewModel.data.first { it == userData }
        profileViewModel.dataDraft.first { it == userData }

        val date = Date(1577836800000L) // 2020-01-01
        profileViewModel.onEvent(ProfileEvent.SaveNewWeight("-5", date))
        advanceUntilIdle()

        assertThat(profileViewModel.errorMessage.value).
        isEqualTo(R.string.userData_error_weight_required)
        verify(userDataRepository, org.mockito.kotlin.never()).addWeight(org.mockito.kotlin.any())
    }

    @Test
    fun `saveNewWeight with invalid date sets error message`() = runTest {
        val birthdate = Date(946684800000L) // 2000-01-01
        val userData = UserData(
            username = "TestUser",
            birthdate = birthdate,
            gender = Gender.MALE,
            height = 180.0,
            weight = 80.0,
            targetWeight = 75.0,
            activityLevel = ActivityLevel.REGULARLY,
            weightGoal = WeightGoal.LOSE_WEIGHT,
            age = 25,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )
        whenever(userDataRepository.getUserData()).thenReturn(userData)
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)

        val invalidDate = Date(915148800000L) // 1999-01-01, which is before the birthdate
        profileViewModel.onEvent(ProfileEvent.SaveNewWeight("80", invalidDate))
        advanceUntilIdle()

        assertThat(profileViewModel.errorMessage.value)
            .isEqualTo(R.string.userData_error_invalid_date)
        verify(userDataRepository, org.mockito.kotlin.never()).addWeight(org.mockito.kotlin.any())
    }
    @Test
    fun `onSaveLanguageClick saves language and restarts app`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "TestUser",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 180.0,
                weight = 80.0,
                targetWeight = 75.0,
                activityLevel = ActivityLevel.REGULARLY,
                weightGoal = WeightGoal.LOSE_WEIGHT,
                age = 25,
                dailyCaloriesGoal = 0,
                proteinGoal = 0,
                carbsGoal = 0,
                fatsGoal = 0
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
        val event = async { profileViewModel.events.first() }
        profileViewModel.onEvent(ProfileEvent.SaveLanguage(Language.ENGLISH))
        advanceUntilIdle()

        verify(appSettingsRepository).setLanguage(Language.ENGLISH)

        assertThat(event.await()).isEqualTo(ProfileEvent.RestartApp)
    }
    @Test
    fun `onChangeThemeClick puts dark theme and saves in repository`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "TestUser",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 180.0,
                weight = 80.0,
                targetWeight = 75.0,
                activityLevel = ActivityLevel.REGULARLY,
                weightGoal = WeightGoal.LOSE_WEIGHT,
                age = 25,
                dailyCaloriesGoal = 0,
                proteinGoal = 0,
                carbsGoal = 0,
                fatsGoal = 0
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)

        profileViewModel.onEvent(ProfileEvent.ChangeTheme(ThemeSetting.DARK))
        advanceUntilIdle()

        verify(appSettingsRepository).setTheme(true)
        assertThat(AppThemeState.currentTheme.value)
            .isEqualTo(ThemeSetting.DARK)
    }

    @Test
    fun `onChangeThemeClick sets light theme and saves in repository`() = runTest {
        whenever(userDataRepository.getUserData()).thenReturn(
            UserData(
                username = "TestUser",
                birthdate = Date(1234567890L),
                gender = Gender.MALE,
                height = 180.0,
                weight = 80.0,
                targetWeight = 75.0,
                activityLevel = ActivityLevel.REGULARLY,
                weightGoal = WeightGoal.LOSE_WEIGHT,
                age = 25,
                dailyCaloriesGoal = 0,
                proteinGoal = 0,
                carbsGoal = 0,
                fatsGoal = 0
            )
        )
        profileViewModel = ProfileViewModel(userDataRepository, appSettingsRepository)

        profileViewModel.onEvent(ProfileEvent.ChangeTheme(ThemeSetting.LIGHT))
        advanceUntilIdle()

        verify(appSettingsRepository).setTheme(false)
        assertThat(AppThemeState.currentTheme.value)
            .isEqualTo(ThemeSetting.LIGHT)
    }


}