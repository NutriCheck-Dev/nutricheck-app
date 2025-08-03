package com.frontend.nutricheck.client.ui.view_model.dashboard

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * UI State for displaying calorie history and goal.
 */
data class CalorieHistoryState(

    val calorieHistory: List<Int> = emptyList(),
    val calorieGoal : Int = 0
)

/**
 * Sealed interface representing events related to calorie history.
 */
sealed interface CalorieHistoryEvent {
    /**
     * Event to display the calorie history for a specified number of days.
     * @property days The number of days to fetch the calorie history for.
     */
    data class DisplayCalorieHistory(val days: Int) : CalorieHistoryEvent
}

@HiltViewModel
class CalorieHistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: UserDataRepository
) : BaseViewModel() {

    private val _calorieHistoryState = MutableStateFlow(CalorieHistoryState())
    val calorieHistoryState = _calorieHistoryState.asStateFlow()

    private val _events = MutableSharedFlow<CalorieHistoryEvent>()
    val events: SharedFlow<CalorieHistoryEvent> = _events.asSharedFlow()


    /**
     * Handles events related to calorie history.
     */
    fun onEvent(event: CalorieHistoryEvent) {
        when (event) {
            is CalorieHistoryEvent.DisplayCalorieHistory -> displayCalorieHistory(event.days)
        }
    }

    /**
     * Fetches the calorie history for the last 'days' days and updates the state.
     * @param days The number of days to fetch the calorie history for.
     */
    fun displayCalorieHistory(days: Int) {
       viewModelScope.launch {
           val calorieGoal = userDataRepository.getDailyCalorieGoal()
           val dateRange = getDateRange(days)
           val calorieHistory: List<Int> = dateRange.map { date ->
               historyRepository.getCaloriesOfDay(date)
           }

           _calorieHistoryState.update { it.copy(calorieHistory = calorieHistory,
               calorieGoal = calorieGoal) }
       }
   }

    /**
     * Generates a list of dates for the last 'days' days, starting from today.
     * @param days The number of days to generate dates for.
     */
    private fun getDateRange(days: Int): List<Date> {
        val today = Calendar.getInstance()
        return (0 until days).map { i ->
            Calendar.getInstance().apply {
                time = today.time
                add(Calendar.DAY_OF_YEAR, -i)
            }.time
        }.reversed()
    }

}