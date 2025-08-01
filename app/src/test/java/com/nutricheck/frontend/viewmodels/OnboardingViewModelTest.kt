package com.nutricheck.frontend.viewmodels

import android.content.Context
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.OnboardingEvent
import com.frontend.nutricheck.client.ui.view_model.OnboardingViewModel
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
import org.mockito.kotlin.whenever
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(appContext.getString(any())).thenReturn("error message")
        onboardingViewModel = OnboardingViewModel(
            appSettingsRepository, userDataRepository, appContext)
    }
    @Mock
    private lateinit var appContext: Context
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
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
    }
    @Test
    fun `enterName with valid name updates state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterName("John Doe"))
        assertThat(onboardingViewModel.data.value.username).isEqualTo("John Doe")
    }
    @Test
    fun `enterBirthdate with null sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterBirthdate(null))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
    }
    @Test
    fun `enterBirthdate with valid date updates state`() = runTest {
        val validDate = Date(1234567890L)
        onboardingViewModel.onEvent(OnboardingEvent.EnterBirthdate(validDate))
        assertThat(onboardingViewModel.data.value.birthdate).isEqualTo(validDate)
    }

    @Test
    fun `enterGender with null sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterGender(null))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
    }

    @Test
    fun `enterGender with valid gender updates state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterGender(Gender.MALE))
        assertThat(onboardingViewModel.data.value.gender).isEqualTo(Gender.MALE)
    }

    @Test
    fun `enterHeight with invalid height sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight(""))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight("-10"))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
    }

    @Test
    fun `enterHeight with valid height updates state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight("180"))
        assertThat(onboardingViewModel.data.value.height).isEqualTo(180.0)
    }

    @Test
    fun `enterWeight with invalid weight sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight(""))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight("-5"))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
    }

    @Test
    fun `enterWeight with valid weight updates state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight("75"))
        assertThat(onboardingViewModel.data.value.weight).isEqualTo(75.0)
    }

    @Test
    fun `enterSportFrequency with null sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterSportFrequency(null))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
    }

    @Test
    fun `enterSportFrequency with valid activity level updates state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterSportFrequency(ActivityLevel.FREQUENTLY))
        assertThat(onboardingViewModel.data.value.activityLevel).isEqualTo(ActivityLevel.FREQUENTLY)
    }

    @Test
    fun `enterWeightGoal with null sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeightGoal(null))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
    }

    @Test
    fun `enterWeightGoal with valid goal updates state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeightGoal(WeightGoal.GAIN_WEIGHT))
        assertThat(onboardingViewModel.data.value.weightGoal).isEqualTo(WeightGoal.GAIN_WEIGHT)
    }

    @Test
    fun `enterTargetWeight with invalid target weight sets error state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterTargetWeight(""))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
        onboardingViewModel.onEvent(OnboardingEvent.EnterTargetWeight("-5"))
        assertThat(onboardingViewModel.uiState.value)
            .isInstanceOf(BaseViewModel.UiState.Error::class.java)
    }

    @Test
    fun `enterTargetWeight with valid target weight updates state`() = runTest {
        onboardingViewModel.onEvent(OnboardingEvent.EnterName("Max"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterBirthdate(Date(946684800000L)))
        onboardingViewModel.onEvent(OnboardingEvent.EnterGender(Gender.MALE))
        onboardingViewModel.onEvent(OnboardingEvent.EnterHeight("180"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeight("80"))
        onboardingViewModel.onEvent(OnboardingEvent.EnterSportFrequency(ActivityLevel.REGULARLY))
        onboardingViewModel.onEvent(OnboardingEvent.EnterWeightGoal(WeightGoal.LOSE_WEIGHT))
        onboardingViewModel.onEvent(OnboardingEvent.EnterTargetWeight("70"))
        assertThat(onboardingViewModel.data.value.targetWeight).isEqualTo(70.0)
    }

    @Test
    fun `completeOnboarding persists user data and adds weight`() = runTest {
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