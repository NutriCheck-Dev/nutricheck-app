package com.frontend.nutricheck.client.ui.view_model.onboarding

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseOnboardingViewModel : BaseViewModel () {

    abstract fun startOnboarding()

    abstract fun completeOnboarding()


}