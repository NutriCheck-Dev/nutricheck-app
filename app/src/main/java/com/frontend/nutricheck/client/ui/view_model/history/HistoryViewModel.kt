package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.ui.view_model.history.BaseHistoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

data class HistoryState(
    val selectedDate: Date = Date(),
    val nutritionOfDay: Map<String, Int> = emptyMap(),
    val mealsOfDay: List<Meal> = emptyList(),
    val foodId: String = "",
    val totalCalories: Int = 0,
    val isSwitched: Boolean = false
)

sealed interface HistoryEvent {
    data class DisplayNutritionOfDay(val day: String) : HistoryEvent
    data class DisplayMealsOfDay(val day: String) : HistoryEvent
    data class FoodClicked(val foodId: String) : HistoryEvent
    data class DetailsClick(val detailsId: String) : HistoryEvent
    data class TotalCaloriesClick(val totalCalories: Int) : HistoryEvent
    data class SwitchClick(val isSwitched: Boolean) : HistoryEvent
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    initialState: HistoryState = HistoryState()
) : BaseHistoryViewModel() {
    private val _historyState = MutableStateFlow(HistoryState())
    val createRecipeState = _historyState.asStateFlow()

    val _events = MutableSharedFlow<HistoryEvent>()
    val events: SharedFlow<HistoryEvent> = _events.asSharedFlow()

    fun onEvent(event: HistoryEvent) {}

    override fun onAddEntryClick() {}

    override fun selectDate(date: String) {}
    override fun displayNutritionOfDay(day: String) {}
    override fun displayMealsOfDay(day: String) {}
    override fun onFoodClicked() {}
    override fun onDetailsClick() {}
    override fun onTotalCaloriesClick() {}
    override fun onSwitchClick() {}
}