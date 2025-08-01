package com.frontend.nutricheck.client.ui.view_model.dashboard.calorie_history

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class CalorieHistoryState(

    val calorieHistory: List<Int> = emptyList(),
    val calorieGoal : Int = 0
)

sealed interface CalorieHistoryEvent {
    data class DisplayCalorieHistory(val days: Int) : CalorieHistoryEvent
}

@HiltViewModel
class CalorieHistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: UserDataRepository
) : BaseCalorieHistoryViewModel() {

    private val _calorieHistoryState = MutableStateFlow(CalorieHistoryState())
    val calorieHistoryState = _calorieHistoryState.asStateFlow()

    private val _events = MutableSharedFlow<CalorieHistoryEvent>()
    val events: SharedFlow<CalorieHistoryEvent> = _events.asSharedFlow()



    fun onEvent(event: CalorieHistoryEvent) {
        when (event) {
            is CalorieHistoryEvent.DisplayCalorieHistory -> displayCalorieHistory(event.days)
        }
    }

   override fun displayCalorieHistory(days: Int) {
       viewModelScope.launch {
           val calorieGoal = userDataRepository.getDailyCalorieGoal()
           val dateRange = getDateRange(days)
           val calorieHistory: List<Int> = dateRange.map { date ->
               historyRepository.getCaloriesOfDay(date)
           }

           _calorieHistoryState.update { it.copy(calorieHistory = calorieHistory, calorieGoal = calorieGoal) }
       }
   }

    // Gibt eine Liste von Datumsobjekten zurück, beginnend bei heute rückwärts
    private fun getDateRange(days: Int): List<Date> {
        val today = java.util.Calendar.getInstance()
        return (0 until days).map { i ->
            java.util.Calendar.getInstance().apply {
                time = today.time
                add(java.util.Calendar.DAY_OF_YEAR, -i)
            }.time
        }.reversed() // Optional: Ältestes Datum zuerst, falls du das willst
    }

}