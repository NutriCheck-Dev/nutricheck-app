package com.frontend.nutricheck.client.ui.view_model.dashboard.daily_calories

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

data class DailyCalorieState(
    val dailyCalories: Int = 0,
    val calorieGoal: Int = 0,
)

@HiltViewModel
class DailyCalorieViewModel @Inject constructor(
    private val userDataRepository : UserDataRepository,
    private val historyRepository : HistoryRepository
) : BaseDailyCalorieViewModel() {

    val _dailyCalorieState = MutableStateFlow(DailyCalorieState())
    val dailyCalorieState = _dailyCalorieState.asStateFlow()
    init {
        displayDailyCalories()
    }

     override fun displayDailyCalories() {
         val currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
         viewModelScope.launch {
             val calories = historyRepository.getCaloriesOfDay(currentDate) // z.B. 850
             val goal = userDataRepository.getDailyCalorieGoal()
             _dailyCalorieState.value = DailyCalorieState(
                 dailyCalories = calories,
                 calorieGoal = goal
             )
         }
     }

}