package com.frontend.nutricheck.client.ui.view_model.dashboard.calorie_history

import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class CalorieHistoryState(
    //val selectedTimePeriod: String = "", falls gepickt gespeichert werden soll
    val caloriesHistory: List<Float> = emptyList(),
    val calorieGoal : Int = 0
)

sealed interface CalorieHistoryEvent {
    data class DisplayCalorieHistory(val timePeriod: String) : CalorieHistoryEvent
    data class SelectTimePeriod(val timePeriod: String) : CalorieHistoryEvent
}

@HiltViewModel
class CalorieHistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: UserDataRepository
) : BaseCalorieHistoryViewModel() {

    private val _calorieHistoryState = MutableStateFlow(CalorieHistoryState())
    val calorieHistoryState = _calorieHistoryState.asStateFlow()

    val _events = MutableSharedFlow<CalorieHistoryEvent>()
    val events: SharedFlow<CalorieHistoryEvent> = _events.asSharedFlow()



    fun onEvent(event: CalorieHistoryEvent) {}

   override fun displayCalorieHistory(timePeriod: String) {
       // Implementation for displaying calorie history
   }
}