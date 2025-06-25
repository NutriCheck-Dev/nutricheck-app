package com.frontend.nutricheck.client.ui.view_model.dashboard

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseDashboardViewModel : BaseViewModel() {

    abstract fun displayDailyCalories()
    abstract fun displayDailyMacros()
    abstract fun displayWeightHistory()
    abstract fun displayCalorieHistory()
}
