package com.frontend.nutricheck.client.ui.view_model.dashboard.dashboard_daily_cal

import com.frontend.nutricheck.client.ui.view_model.dashboard_daily_cal.BaseDailyCalorieViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DailyCalorieState(
    val dailyCalories: Int = 0
)

@HiltViewModel
class DailyCalorieViewModel @Inject constructor(
    initialState: DailyCalorieState = DailyCalorieState()
) : BaseDailyCalorieViewModel() {

    val _dalyCalorieState = MutableStateFlow(DailyCalorieState())
    val dailyCalorieState = _dalyCalorieState.asStateFlow()

     override fun displayDailyCalories() {
         // Implementation for displaying daily calories
     }

}
