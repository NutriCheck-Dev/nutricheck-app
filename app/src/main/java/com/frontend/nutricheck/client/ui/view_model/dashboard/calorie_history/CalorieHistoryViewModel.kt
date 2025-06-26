package com.frontend.nutricheck.client.ui.view_model.dashboard.calorie_history

import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class CalorieHistoryState(
    val timePeriods: List<String> = emptyList(),
    val selectedTimePeriod: String = "",
    val caloriesHistory: List<Pair<String, Int>> = emptyList()
)

sealed interface CalorieHistoryEvent {
    data class DisplayCalorieHistory(val timePeriod: String) : CalorieHistoryEvent
    data class SelectTimePeriod(val timePeriod: String) : CalorieHistoryEvent
}

@HiltViewModel
class CalorieHistoryViewModel @Inject constructor() : BaseCalorieHistoryViewModel() {

    private val _calorieHistoryState = MutableStateFlow(CalorieHistoryState())
    val calorieHistoryState = _calorieHistoryState.asStateFlow()

    val _events = MutableSharedFlow<CalorieHistoryEvent>()
    val events: SharedFlow<CalorieHistoryEvent> = _events.asSharedFlow()

    fun onEvent(event: CalorieHistoryEvent) {}

   override fun displayCalorieHistory(timePeriod: String) {
       // Implementation for displaying calorie history
   }
}