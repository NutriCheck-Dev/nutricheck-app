package com.frontend.nutricheck.client.ui.view_model

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.utils.UserDataUtilsLogic.toDoubleOrNullFlexible
import com.frontend.nutricheck.client.ui.view_model.utils.UserDataUtilsLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * This interface defines the events that can occur during the onboarding process.
 * It includes events for handling user input, navigation, and user interaction.
 */
sealed interface OnboardingEvent {
    // collected events, which handle data input
    data class EnterName(val name: String) : OnboardingEvent
    data class EnterBirthdate(val birthdate: Date?) : OnboardingEvent
    data class EnterGender(val gender: Gender?) : OnboardingEvent
    data class EnterHeight(val height: String) : OnboardingEvent
    data class EnterWeight(val weight: String) : OnboardingEvent
    data class EnterSportFrequency(val activityLevel: ActivityLevel?) : OnboardingEvent
    data class EnterWeightGoal(val weightGoal: WeightGoal?) : OnboardingEvent
    data class EnterTargetWeight(val targetWeight: String) : OnboardingEvent
    // collected events, which lead to navigation
    object CompleteOnboarding : OnboardingEvent
    // Navigation events
    object StartOnboarding : OnboardingEvent
    object NavigateToName : OnboardingEvent
    object NavigateToBirthdate : OnboardingEvent
    object NavigateToGender : OnboardingEvent
    object NavigateToHeight : OnboardingEvent
    object NavigateToWeight : OnboardingEvent
    object NavigateToSportFrequency : OnboardingEvent
    object NavigateToWeightGoal : OnboardingEvent
    object NavigateToTargetWeight : OnboardingEvent
    object NavigateToDashboard : OnboardingEvent
}

/**
 * Data class representing the state of the onboarding process. It contains user input data.
 */
data class OnboardingState(
    val username: String = "",
    val birthdate: Date? = null,
    val gender: Gender? = null,
    val height: Double = 0.0,
    val weight: Double = 0.0,
    var activityLevel: ActivityLevel? = null,
    var weightGoal: WeightGoal? = null,
    var targetWeight: Double = 0.0

)

/**
 * ViewModel for handling onboarding logic and state.
 *
 * @property appSettingRepository Repository for app settings.
 * @property userDataRepository Repository for user data.
 * @property appContext Application context for accessing resources.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val userDataRepository: UserDataRepository,
    @ApplicationContext private val appContext: Context
) : BaseViewModel() {

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    private val _data = MutableStateFlow(OnboardingState())
    val data: StateFlow<OnboardingState> = _data.asStateFlow()

    /**
     * Handles onboarding events and updates the state accordingly.
     */
    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.StartOnboarding -> { emitEvent(OnboardingEvent.NavigateToName) }

            is OnboardingEvent.CompleteOnboarding -> completeOnboarding()
            is OnboardingEvent.EnterName -> enterName(event.name)
            is OnboardingEvent.EnterBirthdate -> enterBirthdate(event.birthdate)
            is OnboardingEvent.EnterGender -> enterGender(event.gender)
            is OnboardingEvent.EnterHeight -> enterHeight(event.height)
            is OnboardingEvent.EnterWeight -> enterWeight(event.weight)
            is OnboardingEvent.EnterSportFrequency -> enterSportFrequency(event.activityLevel)
            is OnboardingEvent.EnterWeightGoal -> enterWeightGoal(event.weightGoal)
            is OnboardingEvent.EnterTargetWeight -> enterTargetWeight(event.targetWeight)
            else -> { /* other navigation events are emitted in enter*functions */}
        }
    }
    private fun enterName(name: String) {
        val errorMessage = UserDataUtilsLogic.isNameInvalid(name)
        if (errorMessage != null) {
            setError(appContext.getString(errorMessage))
            return
        }
        setReady()
        _data.update { it.copy(username = name) }
        emitEvent(OnboardingEvent.NavigateToBirthdate)
    }

    private fun enterBirthdate(birthdate: Date?) {
        val errorMessage = UserDataUtilsLogic.isBirthdateInvalid(birthdate)
        if (errorMessage != null) {
            setError(appContext.getString(errorMessage))
            return
        }
        setReady()
        _data.update { it.copy(birthdate = birthdate) }
        emitEvent(OnboardingEvent.NavigateToGender)
    }

    private fun enterGender(gender: Gender?) {
        if (gender == null) {
            setError(appContext.getString(R.string.userData_error_gender_required))
            return
        }
        setReady()
        _data.update { it.copy(gender = gender) }
        emitEvent(OnboardingEvent.NavigateToHeight)
    }

    private fun enterHeight(height: String) {
        val heightValue = height.toDoubleOrNullFlexible()
        val errorMessage = UserDataUtilsLogic.isHeightInvalid(heightValue)
        if (errorMessage != null) {
            setError(appContext.getString(errorMessage))
            return
        }
        setReady()
        _data.update { it.copy(height = heightValue!!) }
        emitEvent(OnboardingEvent.NavigateToWeight)
    }

    private fun enterWeight(weight: String) {
        val weightValue = weight.toDoubleOrNullFlexible()
        val errorMessage = UserDataUtilsLogic.isWeightInvalid(weightValue)
        if (errorMessage != null) {
            setError(appContext.getString(errorMessage))
            return
        }
        setReady()
        _data.update { it.copy(weight = weightValue!!) }
        emitEvent(OnboardingEvent.NavigateToSportFrequency)
    }

    private fun enterSportFrequency(activityLevel: ActivityLevel?) {
        if (activityLevel == null) {
            setError(appContext.getString(R.string.userData_error_activity_level_required))
            return
        }
        setReady()
        _data.update { it.copy(activityLevel = activityLevel) }
        emitEvent(OnboardingEvent.NavigateToWeightGoal)
    }

    private fun enterWeightGoal(weightGoal: WeightGoal?) {
        if (weightGoal == null) {
            setError(appContext.getString(R.string.userData_error_goal_required))
            return
        }
        setReady()
        _data.update { it.copy(weightGoal = weightGoal) }
        emitEvent(OnboardingEvent.NavigateToTargetWeight)
    }

    private fun enterTargetWeight(targetWeight: String) {
        val targetWeightValue = targetWeight.toDoubleOrNullFlexible()
        val errorMessage = UserDataUtilsLogic.isTargetWeightInvalid(targetWeightValue)
        if (errorMessage != null) {
            setError(appContext.getString(errorMessage))
            return
        }
        setReady()
        _data.update { it.copy(targetWeight = targetWeightValue!!) }
        completeOnboarding()
    }
    private fun completeOnboarding() {
        val newUserData = UserData(
            username = _data.value.username,
            birthdate = _data.value.birthdate!!,
            gender = _data.value.gender!!,
            height = _data.value.height,
            weight = _data.value.weight,
            activityLevel = _data.value.activityLevel!!,
            weightGoal = _data.value.weightGoal!!,
            targetWeight = _data.value.targetWeight,
            age = UserDataUtilsLogic.calculateAge(_data.value.birthdate!!),
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )
        val userDataWithCalc = UserDataUtilsLogic.calculateNutrition(newUserData)
        viewModelScope.launch {
            appSettingRepository.setOnboardingCompleted()
            userDataRepository
                .addUserDataAndAddWeight(userDataWithCalc, Weight(_data.value.weight, Date()))
        }
        emitEvent(OnboardingEvent.NavigateToDashboard)
    }
    private fun emitEvent(event: OnboardingEvent) = viewModelScope.launch { _events.emit(event) }

}