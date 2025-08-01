package com.frontend.nutricheck.client.ui.view_model.dashboard

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyMacrosState(
    val dailyProtein: Int = 0,
    val dailyProteinGoal: Int = 0,
    val dailyCarbs: Int = 0,
    val dailyCarbsGoal: Int = 0,
    val dailyFat: Int = 0,
    val dailyFatGoal: Int = 0,
)
@HiltViewModel
class DailyMacrosViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: UserDataRepository
) : BaseViewModel() {

    private val _dailyMacrosState = MutableStateFlow(DailyMacrosState())
    val dailyMacrosState = _dailyMacrosState.asStateFlow()

     fun displayDailyMacros() {
        viewModelScope.launch {
            val macroGoals = userDataRepository.getNutrientGoal()
            val dailyMacros = historyRepository.getDailyMacros()
            _dailyMacrosState.update {
                it.copy(
                    dailyCarbsGoal = macroGoals[0],
                    dailyProtein = macroGoals[1],
                    dailyFatGoal = macroGoals[2],
                    dailyCarbs = dailyMacros[0],
                    dailyProteinGoal = dailyMacros[1],
                    dailyFat = dailyMacros[2]
                )
            }

        }
    }
}