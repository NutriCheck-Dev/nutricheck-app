package com.frontend.nutricheck.client.ui.view_model.onboarding


import androidx.compose.ui.res.stringResource
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.nutricheck.frontend.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

sealed interface OnboardingEvent {
    data class StartOnboarding(val step: Int) : OnboardingEvent
    data class CompleteOnboarding(val success: Boolean) : OnboardingEvent
    data class EnterName(val name: String) : OnboardingEvent
    data class EnterBirthdate(val birthdate: String) : OnboardingEvent
    data class EnterGender(val gender: String) : OnboardingEvent
    data class EnterHeight(val height: Double) : OnboardingEvent
    data class EnterWeight(val weight: Double) : OnboardingEvent
    data class EnterSportFrequency(val activityLevel: ActivityLevel) : OnboardingEvent
    data class EnterWeightGoal(val weightGoal: WeightGoal) : OnboardingEvent
    data class EnterTargetWeight(val targetWeight: Double) : OnboardingEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor() : BaseOnboardingViewModel() {

    val _events = MutableSharedFlow<OnboardingEvent>()
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    var username: String = ""
    var birthdate: String = ""
    var gender: Gender? = null
    var height: Double = 0.0
    var weight: Double = 0.0
    var activityLevel: ActivityLevel? = null
    var weightGoal: WeightGoal? = null
    var targetWeight: Double = 0.0



    fun onEvent(event: OnboardingEvent) {}

    override fun startOnboarding() {
        TODO("navigate to name step")
    }

    override fun completeOnboarding() {
        TODO("send collected data to the model")
        TODO("navigate to dashboard")
    }

    override fun enterName(name: String) {
        if (name.isBlank()) {
            TODO("show error message")
            return
        }
        username = name
        TODO("navigate to birthdate step")
    }

    override fun enterBirthdate(birthdate: String) {
        if (!validateBirthdate(birthdate)) {
            TODO("show error message")
            return
        }
        this.birthdate = birthdate
        TODO("navigate to gender step")

    }

    override fun enterGender(gender: Gender?) {
        if (gender == null) {
            TODO("show error message")
            return
        }
        this.gender = gender
        TODO("navigate to height step")
    }

    override fun enterHeight(height: String) {
        val heightAsDouble : Double? = height.toDoubleOrNull()
        if (heightAsDouble == null || heightAsDouble <= 0) {
            TODO("show error message")
            return
        }
        this.height = heightAsDouble
        TODO("navigate to weight step")
    }

    override fun enterWeight(weight: String) {
        val weightAsDouble: Double? = weight.toDoubleOrNull()
        if (weightAsDouble == null || weightAsDouble <= 0) {
            TODO("show error message")
            return
        }
        this.weight = weightAsDouble
        TODO("navigate to sport frequency step")
    }

    override fun enterSportFrequency(activityLevel: ActivityLevel?) {
        if (activityLevel == null) {
            TODO("show error message")
            return
        }
        this.activityLevel = activityLevel
        TODO("navigate to weight goal step")
    }

    override fun enterWeightGoal(weightGoal: WeightGoal?) {
        if (weightGoal == null) {
            TODO("show error message")
            return
        }
        this.weightGoal = weightGoal
        TODO("navigate to target weight step")
    }

    override fun enterTargetWeight(targetWeight: String) {
        val targetWeightAsDouble: Double? = targetWeight.toDoubleOrNull()
        if (targetWeightAsDouble == null || targetWeightAsDouble <= 0) {
            TODO("show error message")
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