package com.frontend.nutricheck.client.ui.view_model.onboarding

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseOnboardingViewModel : BaseViewModel () {

    abstract fun startOnboarding()
    abstract fun enterName(name: String)
    abstract fun enterBirthdate(birthdate: String)
    abstract fun enterGender(gender: String)
    abstract fun enterHeight(height: Double)
    abstract fun enterWeight(weight: Double)
    abstract fun enterSportFrequency(sportFrequency: SportFrequency)
    abstract fun enterWeightGoal(weightGoal: WeightGoal)
    abstract fun enterTargetWeight(targetWeight: Double)
    abstract fun completeOnboarding()


}