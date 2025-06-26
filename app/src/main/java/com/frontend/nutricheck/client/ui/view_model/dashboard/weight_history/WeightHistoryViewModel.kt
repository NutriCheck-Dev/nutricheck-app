package com.frontend.nutricheck.client.ui.view_model.dashboard.weight_history

import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class WeightHistoryState(
    val timePeriods: List<String> = emptyList(),
    val selectedTimePeriod: String = "",
    val weightData: List<Float> = emptyList(),
)

sealed interface WeightHistoryEvent {
    data class DisplayWeightHistory(val timePeriod: String) : WeightHistoryEvent
}

@HiltViewModel
class WeightHistoryViewModel @Inject constructor(
    initialState: WeightHistoryState = WeightHistoryState(),
) : BaseWeightHistoryViewModel() {

    private val _weightHistoryState = MutableStateFlow(WeightHistoryState())
    val weightHistoryState = _weightHistoryState.asStateFlow()

    private val _events = MutableSharedFlow<WeightHistoryEvent>()
    val events: SharedFlow<WeightHistoryEvent> = _events.asSharedFlow()

   override fun displayWeightHistory(timePeriod: String) {
       // Implementation for displaying weight history
   }
}