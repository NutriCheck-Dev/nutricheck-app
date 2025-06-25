package com.frontend.nutricheck.client.ui.view_model


import com.frontend.nutricheck.client.ui.view_model.onboarding.BaseOnboardingViewModel
import com.frontend.nutricheck.client.ui.view_model.onboarding.SportFrequency
import com.frontend.nutricheck.client.ui.view_model.onboarding.WeightGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

sealed interface OnboardingEvent {
    data class StartOnboarding(val step: Int) : OnboardingEvent
    data class CompleteOnboarding(val success: Boolean) : OnboardingEvent
    data class EnterName(val name: String) : OnboardingEvent
    data class EnterBirthdate(val birthdate: String) : OnboardingEvent
    data class EnterGender(val gender: String) : OnboardingEvent
    data class EnterHeight(val height: Double) : OnboardingEvent
    data class EnterWeight(val weight: Double) : OnboardingEvent
    data class EnterSportFrequency(val sportFrequency: SportFrequency) : OnboardingEvent
    data class EnterWeightGoal(val weightGoal: WeightGoal) : OnboardingEvent
    data class EnterTargetWeight(val targetWeight: Double) : OnboardingEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor() : BaseOnboardingViewModel() {

    val _events = MutableSharedFlow<OnboardingEvent>()
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    fun onEvent(event: OnboardingEvent) {}

    override fun startOnboarding() {
        // Logic to start the onboarding process
    }

    override fun completeOnboarding() {
        // Logic to complete the onboarding process
    }

    override fun enterName(name: String) {
        // Logic to enter the user's name
    }

    override fun enterBirthdate(birthdate: String) {
        // Logic to enter the user's birthdate
    }

    override fun enterGender(gender: String) {
        // Logic to enter the user's gender
    }

    override fun enterHeight(height: Double) {
        // Logic to enter the user's height
    }

    override fun enterWeight(weight: Double) {
        // Logic to enter the user's weight
    }

    override fun enterSportFrequency(sportFrequency: SportFrequency) {
        // Logic to enter the user's sport frequency
    }

    override fun enterWeightGoal(weightGoal: WeightGoal) {
        // Logic to enter the user's weight goal
    }

    override fun enterTargetWeight(targetWeight: Double) {
        // Logic to enter the user's target weight
    }
}