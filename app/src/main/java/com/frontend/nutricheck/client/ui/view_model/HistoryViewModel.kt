package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class HistoryState(
    val selectedDate: Date = Date(),
    val nutritionOfDay: Map<String, Int> = emptyMap(),
    val mealsGrouped: Map<DayTime, List<Meal>> = emptyMap(),
    val mealId: String = "",
    val foodId: String = "",
    val recipeId: String = "",
    val totalCalories: Int = 0,
    val goalCalories: Int = 0,
    val switched: Boolean = false
)

sealed interface HistoryEvent {
    data class AddEntryClick(val day: Date, val dayTime: DayTime) : HistoryEvent
    data class FoodClicked(val mealId: String, val foodId: String) : HistoryEvent
    data class RecipeClicked(val mealId:String, val recipeId: String) : HistoryEvent
    data class RemoveMealItem(val mealItem: MealItem) : HistoryEvent
    data class DetailsClick(val detailsId: String) : HistoryEvent
    data class SelectDate(val day: Date) : HistoryEvent
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: UserDataRepository
) : BaseViewModel() {
    private val _historyState = MutableStateFlow(HistoryState())
    val historyState = _historyState.asStateFlow()

    private val _events = MutableSharedFlow<HistoryEvent>()
    val events: SharedFlow<HistoryEvent> = _events.asSharedFlow()

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.AddEntryClick -> onAddEntryClick(event.day, event.dayTime)
            is HistoryEvent.FoodClicked -> onFoodClicked(event.mealId, event.foodId)
            is HistoryEvent.RecipeClicked -> onRecipeClicked(event.mealId, event.recipeId)
            is HistoryEvent.RemoveMealItem -> onRemoveMealItem(event.mealItem)
            is HistoryEvent.DetailsClick -> onDetailsClick(event.detailsId)
            is HistoryEvent.SelectDate -> selectDate(event.day)
        }
    }
    init {
        selectDate(Date())
    }
    fun onAddEntryClick(day: Date, dayTime: DayTime) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.AddEntryClick(day, dayTime))
        }
    }

    fun selectDate(day: Date) {
        _historyState.update {
            it.copy(
                selectedDate = day
            )
        }
        displayMealsOfDay(day)
        displayCalorieGoal(day)
    }

    private fun displayCalorieGoal(day: Date) {
        viewModelScope.launch {
            val totalCalories = historyRepository.getCaloriesOfDay(day)
            val goalCalories = userDataRepository.getDailyCalorieGoal()
            _historyState.update {
                it.copy(
                    totalCalories = totalCalories,
                    goalCalories = goalCalories
                )
            }
        }
    }

    fun onRemoveMealItem(mealItem: MealItem) {
        viewModelScope.launch {
            historyRepository.removeMealItem(mealItem)
            displayMealsOfDay(_historyState.value.selectedDate)
        }
    }
    fun displayMealsOfDay(day: Date) {
        viewModelScope.launch {
            val meals = historyRepository.getMealsForDay(day)
            val groupedMeals = meals.groupBy { it.dayTime }
            _historyState.update {
                it.copy(
                    mealsGrouped = groupedMeals
                )
            }
        }
    }

    fun onFoodClicked(mealId: String, foodId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.FoodClicked(mealId, foodId))
        }
    }
    fun onRecipeClicked(mealId: String, recipeId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.RecipeClicked(mealId, recipeId))
        }
    }
    fun onDetailsClick(detailsId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.DetailsClick(detailsId))
        }
    }
    //override fun onTotalCaloriesClick(totalCalories: Int) {} TODO
}