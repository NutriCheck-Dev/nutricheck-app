package com.frontend.nutricheck.client.ui.view_model.dashboard

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class DashboardState(
    val dailyCalories: Int = 0,
    val dailyMacros: Map<String, Int> = emptyMap(),
    val weightHistory: List<Pair<String, Float>> = emptyList(),
    val calorieHistory: List<Pair<String, Int>> = emptyList()
)

sealed interface DashboardEvent {
    data class DisplayDailyCalories(val calories: Int) : DashboardEvent
    data class DisplayDailyMacros(val macros: Map<String, Int>) : DashboardEvent
    data class DisplayWeightHistory(val history: List<Pair<String, Float>>) : DashboardEvent
    data class DisplayCalorieHistory(val history: List<Pair<String, Int>>) : DashboardEvent
}

@HiltViewModel
 class DashboardViewModel @Inject constructor() : BaseDashboardViewModel() {

    val data : DashboardState = DashboardState()


    private val _events = MutableSharedFlow<DashboardEvent>()
    val events: SharedFlow<DashboardEvent> = _events.asSharedFlow()
    fun onEvent(event: DashboardEvent) {}

    override fun displayDailyCalories() {
         // Implementation for displaying daily calories
     }

    override fun displayDailyMacros() {
        //TODO("Not yet implemented")
    }

    override fun displayWeightHistory(timePeriod: Int) {
        //TODO("Not yet implemented")
    }
    override fun displayCalorieHistory(timePeriod: Int) {
        //TODO("Not yet implemented")
    }
    override fun displayRecentlyAddedItems() {
        //TODO("Not yet implemented")
    }

 }
