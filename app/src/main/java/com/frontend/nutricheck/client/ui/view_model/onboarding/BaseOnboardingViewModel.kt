package com.frontend.nutricheck.client.ui.view_model.onboarding

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseOnboardingViewModel : BaseViewModel () {

    abstract fun startOnboarding()
    abstract fun enterName(name: String)
    abstract fun enterBirthdate(birthdate: String)
    abstract fun enterGender(gender: String)
    abstract fun enterHeight(height: String)
    abstract fun enterWeight(weight: String)
    abstract fun enterSportFrequency(activityLevel: String)
    abstract fun enterWeightGoal(weightGoal: String)
    abstract fun enterTargetWeight(targetWeight: String)
    abstract fun completeOnboarding()


}