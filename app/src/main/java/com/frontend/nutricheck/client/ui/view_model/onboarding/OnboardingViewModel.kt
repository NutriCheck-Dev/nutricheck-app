package com.frontend.nutricheck.client.ui.view_model.onboarding

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

sealed interface OnboardingEvent {
    data class EnterName(val name: String) : OnboardingEvent
    data class EnterBirthdate(val birthdate: Date?) : OnboardingEvent
    data class EnterGender(val gender: Gender?) : OnboardingEvent
    data class EnterHeight(val height: String) : OnboardingEvent
    data class EnterWeight(val weight: String) : OnboardingEvent
    data class EnterSportFrequency(val activityLevel: ActivityLevel?) : OnboardingEvent
    data class EnterWeightGoal(val weightGoal: WeightGoal?) : OnboardingEvent
    data class EnterTargetWeight(val targetWeight: String) : OnboardingEvent

    object StartOnboarding : OnboardingEvent
    object CompleteOnboarding : OnboardingEvent

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
data class OnboardingState(
    val errorState: Int? = null,
    val username: String = "",
    val birthdate: Date? = null,
    val gender: Gender? = null,
    val height: Double = 0.0,
    val weight: Double = 0.0,
    var activityLevel: ActivityLevel? = null,
    var weightGoal: WeightGoal? = null,
    var targetWeight: Double = 0.0

)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val userDataRepository: UserDataRepository
) : BaseOnboardingViewModel() {

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    private val _data = MutableStateFlow(OnboardingState())
    val data: StateFlow<OnboardingState> = _data.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.StartOnboarding -> startOnboarding()
            is OnboardingEvent.CompleteOnboarding -> completeOnboarding()
            is OnboardingEvent.EnterName -> enterName(event.name)
            is OnboardingEvent.EnterBirthdate -> enterBirthdate(event.birthdate)
            is OnboardingEvent.EnterGender -> enterGender(event.gender)
            is OnboardingEvent.EnterHeight -> enterHeight(event.height)
            is OnboardingEvent.EnterWeight -> enterWeight(event.weight)
            is OnboardingEvent.EnterSportFrequency -> enterSportFrequency(event.activityLevel)
            is OnboardingEvent.EnterWeightGoal -> enterWeightGoal(event.weightGoal)
            is OnboardingEvent.EnterTargetWeight -> enterTargetWeight(event.targetWeight)
            else -> { /* Navigationsevents werden hier nicht behandelt */ }
        }
    }

    override fun startOnboarding() {
        emitEvent(OnboardingEvent.NavigateToName)
    }

    override fun enterName(name: String) {
        if (name.isBlank()) {
            _data.update {
                it.copy(errorState = (R.string.userData_error_name_required))
            }
            return
        }
        _data.update { it.copy(errorState = null) }
        _data.update { it.copy(username = name) }
        emitEvent(OnboardingEvent.NavigateToBirthdate)
    }

    override fun enterBirthdate(birthdate: Date?) {
        if (birthdate == null || Utils.isBirthdateInvalid(birthdate)) {
            _data.update {
                it.copy(errorState = R.string.userData_error_birthdate_required)
            }
            return
        }
        _data.update { it.copy(errorState = null) }
        _data.update { it.copy(birthdate = birthdate) }
        emitEvent(OnboardingEvent.NavigateToGender)
    }

    override fun enterGender(gender: Gender?) {
        if (gender == null) {
            _data.update {
                it.copy(errorState = R.string.userData_error_gender_required)
            }
            return
        }
        _data.update { it.copy(errorState = null) }
        _data.update { it.copy(gender = gender) }
        emitEvent(OnboardingEvent.NavigateToHeight)
    }

    override fun enterHeight(height: String) {
        val heightAsDouble : Double? = height.toDoubleOrNull()
        if (heightAsDouble == null || heightAsDouble <= 0) {
            _data.update {
                it.copy(errorState = R.string.userData_error_height_required)
            }
            return
        }
        _data.update { it.copy(errorState = null) }
        _data.update { it.copy(height = heightAsDouble) }
        emitEvent(OnboardingEvent.NavigateToWeight)
    }

    override fun enterWeight(weight: String) {
        val weightAsDouble: Double? = weight.toDoubleOrNull()
        if (weightAsDouble == null || weightAsDouble <= 0) {
            _data.update {
                it.copy(errorState = R.string.userData_error_weight_required)
            }
            return
        }
        _data.update { it.copy(errorState = null) }
        _data.update { it.copy(weight = weightAsDouble) }
        emitEvent(OnboardingEvent.NavigateToSportFrequency)
    }

    override fun enterSportFrequency(activityLevel: ActivityLevel?) {
        if (activityLevel == null) {
            _data.update {
                it.copy(errorState = R.string.userData_error_activity_level_required)
            }
            return
        }
        _data.update { it.copy(errorState = null) }
        _data.update { it.copy(activityLevel = activityLevel) }
        emitEvent(OnboardingEvent.NavigateToWeightGoal)
    }

    override fun enterWeightGoal(weightGoal: WeightGoal?) {
        if (weightGoal == null) {
            _data.update {
                it.copy(errorState = R.string.userData_error_goal_required)
            }
            return
        }
        _data.update { it.copy(errorState = null) }
        _data.update { it.copy(weightGoal = weightGoal) }
        emitEvent(OnboardingEvent.NavigateToTargetWeight)
    }

    override fun enterTargetWeight(targetWeight: String) {
        val targetWeightAsDouble: Double? = targetWeight.toDoubleOrNull()
        if (targetWeightAsDouble == null || targetWeightAsDouble <= 0.0) {
            _data.update { it.copy(errorState = R.string.userData_error_target_weight_required) }
            return
        }
        _data.update { it.copy(errorState = null) }
        _data.update { it.copy(targetWeight = targetWeightAsDouble) }
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
            age = Utils.calculateAge(_data.value.birthdate!!),
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )
        Utils.calculateNutrition(newUserData)
        viewModelScope.launch {
            userDataRepository.addUserData(newUserData)
            userDataRepository.addWeight(Weight(_data.value.weight, Date()))
            appSettingsRepository.setOnboardingCompleted()
        }
        emitEvent(OnboardingEvent.NavigateToDashboard)
    }
    private fun emitEvent(event: OnboardingEvent) = viewModelScope.launch { _events.emit(event) }

}