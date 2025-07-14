package com.frontend.nutricheck.client.ui.view_model.onboarding
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseOnboardingViewModel : BaseViewModel () {

    abstract fun startOnboarding()
    abstract fun enterName(name: String)
    abstract fun enterBirthdate(birthdate: String)
    abstract fun enterGender(gender: Gender?)
    abstract fun enterHeight(height: String)
    abstract fun enterWeight(weight: String)
    abstract fun enterSportFrequency(activityLevel: ActivityLevel?)
    abstract fun enterWeightGoal(weightGoal: WeightGoal?)
    abstract fun enterTargetWeight(targetWeight: String)
}