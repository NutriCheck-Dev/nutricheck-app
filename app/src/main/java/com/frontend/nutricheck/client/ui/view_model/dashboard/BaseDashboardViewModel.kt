package com.frontend.nutricheck.client.ui.view_model.dashboard

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseDashboardViewModel : BaseViewModel() {

    abstract fun displayDailyCalories()
    abstract fun displayDailyMacros()
    abstract fun displayWeightHistory(timePeriod: Int)
    abstract fun displayCalorieHistory(timePeriod: Int)
    abstract fun displayRecentlyAddedItems()
}
