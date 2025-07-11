package com.frontend.nutricheck.client.ui.view_model.onboarding

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.nutricheck.frontend.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

sealed interface OnboardingEvent {
    data class EnterName(val name: String) : OnboardingEvent
    data class EnterBirthdate(val birthdate: String) : OnboardingEvent
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

@HiltViewModel
class OnboardingViewModel @Inject constructor() : BaseOnboardingViewModel() {

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    private val _errorState = MutableStateFlow<Int?>(null)
    val errorState: StateFlow<Int?> = _errorState.asStateFlow()

    var username: String = ""
    var birthdate: String = ""
    var gender: Gender? = null
    var height: Double = 0.0
    var weight: Double = 0.0
    var activityLevel: ActivityLevel? = null
    var weightGoal: WeightGoal? = null
    var targetWeight: Double = 0.0



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
        viewModelScope.launch {
            _events.emit(OnboardingEvent.NavigateToName)
        }
    }

    override fun completeOnboarding() {
        TODO("send collected data to the model")
        TODO("navigate to dashboard")
    }

    override fun enterName(name: String) {
        if (name.isBlank()) {
            _errorState.value = (R.string.onboarding_error_name_required)
            return
        }
        _errorState.value = null
        username = name
        viewModelScope.launch {
            _events.emit(OnboardingEvent.NavigateToBirthdate)
        }
    }

    override fun enterBirthdate(birthdate: String) {
        if (!validateBirthdate(birthdate)) {
            _errorState.value = R.string.onboarding_error_birthdate_required
            return
        }
        this.birthdate = birthdate
        viewModelScope.launch {
            _events.emit(OnboardingEvent.NavigateToGender)
        }
    }

    override fun enterGender(gender: Gender?) {
        if (gender == null) {
            _errorState.value = R.string.onboarding_error_gender_required
            return
        }
        this.gender = gender
        viewModelScope.launch {
            _events.emit(OnboardingEvent.NavigateToHeight)
        }
    }

    override fun enterHeight(height: String) {
        val heightAsDouble : Double? = height.toDoubleOrNull()
        if (heightAsDouble == null || heightAsDouble <= 0) {
            _errorState.value = R.string.onboarding_error_height_required
            return
        }
        this.height = heightAsDouble
        viewModelScope.launch {
            _events.emit(OnboardingEvent.NavigateToWeight)
        }
    }

    override fun enterWeight(weight: String) {
        val weightAsDouble: Double? = weight.toDoubleOrNull()
        if (weightAsDouble == null || weightAsDouble <= 0) {
            _errorState.value = R.string.onboarding_error_weight_required
            return
        }
        this.weight = weightAsDouble
        viewModelScope.launch {
            _events.emit(OnboardingEvent.NavigateToSportFrequency)
        }
    }

    override fun enterSportFrequency(activityLevel: ActivityLevel?) {
        if (activityLevel == null) {
            _errorState.value = R.string.onboarding_error_activity_level_required
            return
        }
        this.activityLevel = activityLevel
        viewModelScope.launch {
            _events.emit(OnboardingEvent.NavigateToWeightGoal)
        }
    }

    override fun enterWeightGoal(weightGoal: WeightGoal?) {
        if (weightGoal == null) {
            _errorState.value = R.string.onboarding_error_goal_required
            return
        }
        this.weightGoal = weightGoal
        viewModelScope.launch {
            _events.emit(OnboardingEvent.NavigateToTargetWeight)
        }
    }

    override fun enterTargetWeight(targetWeight: String) {
        val targetWeightAsDouble: Double? = targetWeight.toDoubleOrNull()
        if (targetWeightAsDouble == null || targetWeightAsDouble <= 0) {
            _errorState.value = R.string.onboarding_error_target_weight_required
            return
        }
        this.targetWeight = targetWeightAsDouble
        TODO("complete onboarding and navigate to dashboard")
    }

    private fun validateBirthdate(birthdate: String): Boolean {
        if (birthdate.isBlank()) {
            return false
        }
        if (!birthdate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            return false
        }
        return try {
            val parsedBirthdate = LocalDate.parse(birthdate)
            val today = LocalDate.now()
            val hundredYearsAgo = today.minusYears(100)
            !parsedBirthdate.isAfter(today) && !parsedBirthdate.isBefore(hundredYearsAgo)
        } catch (e: DateTimeParseException) {
            false
        }
    }

}