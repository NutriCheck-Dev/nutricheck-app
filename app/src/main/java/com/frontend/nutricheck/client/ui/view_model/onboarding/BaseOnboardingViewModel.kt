package com.frontend.nutricheck.client.ui.view_model.onboarding
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import java.util.Date

abstract class BaseOnboardingViewModel : BaseViewModel () {

    abstract fun startOnboarding()
    abstract fun enterName(name: String)
    abstract fun enterBirthdate(birthdate: Date?)
    abstract fun enterGender(gender: Gender?)
    abstract fun enterHeight(height: String)
    abstract fun enterWeight(weight: String)
    abstract fun enterSportFrequency(activityLevel: ActivityLevel?)
    abstract fun enterWeightGoal(weightGoal: WeightGoal?)
    abstract fun enterTargetWeight(targetWeight: String)
}