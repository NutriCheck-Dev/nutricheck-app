package com.nutricheck.frontend.viewmodels

import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.onboarding.OnboardingEvent
import com.frontend.nutricheck.client.ui.view_model.onboarding.OnboardingViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        onboardingViewModel = OnboardingViewModel(
            appSettingsRepository, userDataRepository)
    }

    @Mock
    private lateinit var appSettingsRepository: AppSettingsRepository
    @Mock
    private lateinit var userDataRepository: UserDataRepository
    private lateinit var onboardingViewModel : OnboardingViewModel

    class MainCoroutineRule : TestWatcher() {
        override fun starting(description : Description?) {
            Dispatchers.setMain(StandardTestDispatcher())
        }
        override fun finished(description: Description?) {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `enterName with blank name sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterName(""))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_name_required)
    }
    @Test
    fun `enterName with valid name clears error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterName("John Doe"))
        assertThat(onboardingViewModel.data.value.errorState).isNull()
        assertThat(onboardingViewModel.data.value.username).isEqualTo("John Doe")
    }
    @Test
    fun `enterBirthdate with null sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterBirthdate(null))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_birthdate_required)
    }
    @Test
    fun `enterBirthdate with invalid date sets error state`() = runTest {
        val invalidDate = Date(-4417632000000L) // 1830-01-01
        onboardingViewModel.onEvent(OnboardingEvent.EnterBirthdate(invalidDate))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_birthdate_required)
    }

    @Test
    fun `enterBirthdate with valid date clears error state and sets birthdate`() = runTest {
        val validDate = Date(1234567890L) // 1970-01-01
        onboardingViewModel.onEvent(OnboardingEvent.EnterBirthdate(validDate))
        assertThat(onboardingViewModel.data.value.errorState).isNull()
        assertThat(onboardingViewModel.data.value.birthdate).isEqualTo(validDate)
    }

    @Test
    fun `enterGender with null sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterGender(null))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_gender_required)
    }

    @Test
    fun `enterGender with valid gender clears error state and sets gender`() = runTest {
        val gender = Gender.MALE
        onboardingViewModel.onEvent(OnboardingEvent.EnterGender(gender))
        assertThat(onboardingViewModel.data.value.errorState).isNull()
        assertThat(onboardingViewModel.data.value.gender).isEqualTo(gender)
    }
    @Test
    fun `enterHeight with invalid height sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight(""))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_height_required)

        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight("-10"))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_height_required)
    }

    @Test
    fun `enterHeight with valid height clears error state and sets height`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight("180"))
        assertThat(onboardingViewModel.data.value.errorState).isNull()
        assertThat(onboardingViewModel.data.value.height).isEqualTo(180.0)
    }

    @Test
    fun `enterWeight with invalid weight sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight(""))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_weight_required)

        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight("-5"))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_weight_required)
    }

    @Test
    fun `enterWeight with valid weight clears error state and sets weight`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight("75"))
        assertThat(onboardingViewModel.data.value.errorState).isNull()
        assertThat(onboardingViewModel.data.value.weight).isEqualTo(75.0)
    }
    @Test
    fun `enterSportFrequency with null sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterSportFrequency(null))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_activity_level_required)
    }

    @Test
    fun `enterSportFrequency with valid activity level clears error state and sets activity level`() = runTest {
        val activityLevel = ActivityLevel.FREQUENTLY
        onboardingViewModel.onEvent(OnboardingEvent.EnterSportFrequency(activityLevel))
        assertThat(onboardingViewModel.data.value.errorState).isNull()
        assertThat(onboardingViewModel.data.value.activityLevel).isEqualTo(activityLevel)
    }

    @Test
    fun `enterWeightGoal with null sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeightGoal(null))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_goal_required)
    }

    @Test
    fun `enterWeightGoal with valid goal clears error state and sets weight goal`() = runTest {
        val weightGoal = WeightGoal.GAIN_WEIGHT
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeightGoal(weightGoal))
        assertThat(onboardingViewModel.data.value.errorState).isNull()
        assertThat(onboardingViewModel.data.value.weightGoal).isEqualTo(weightGoal)
    }

    @Test
    fun `enterTargetWeight with invalid target weight sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterTargetWeight(""))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_target_weight_required)

        onboardingViewModel.onEvent(OnboardingEvent.EnterTargetWeight("-5"))
        assertThat(onboardingViewModel.data.value.errorState)
            .isEqualTo(R.string.userData_error_target_weight_required)
    }

    @Test
    fun `enterTargetWeight with valid target weight clears error state and sets target weight`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterName("Max"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterBirthdate(Date(946684800000L)))
        onboardingViewModel.onEvent(OnboardingEvent.EnterGender(Gender.MALE))
        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight("180"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight("80"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterSportFrequency(ActivityLevel.REGULARLY))
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeightGoal(WeightGoal.LOSE_WEIGHT))
        onboardingViewModel.onEvent(OnboardingEvent.EnterTargetWeight("70"))
        advanceUntilIdle()

        assertThat(onboardingViewModel.data.value.errorState).isNull()
        assertThat(onboardingViewModel.data.value.targetWeight).isEqualTo(70.0)
    }

    @Test
    fun `completeOnboarding persists user data, adds weight and sets onboarding completed`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterName("Max"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterBirthdate(Date(946684800000L)))
        onboardingViewModel.onEvent(OnboardingEvent.EnterGender(Gender.MALE))
        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight("180"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight("80"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterSportFrequency(ActivityLevel.REGULARLY))
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeightGoal(WeightGoal.LOSE_WEIGHT))
        onboardingViewModel.onEvent(OnboardingEvent.EnterTargetWeight("75"))
        advanceUntilIdle()

        verify(userDataRepository).addUserData(any<UserData>())
        verify(userDataRepository).addWeight(any<Weight>())
        verify(appSettingsRepository).setOnboardingCompleted()
    }
}