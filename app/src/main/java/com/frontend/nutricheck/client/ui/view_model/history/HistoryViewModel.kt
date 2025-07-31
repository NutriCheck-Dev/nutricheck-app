package com.frontend.nutricheck.client.ui.view_model.history

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
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
    val foodId: String = "",
    val totalCalories: Int = 0,
    val goalCalories: Int = 0,
    val switched: Boolean = false
)

sealed interface HistoryEvent {
    data class AddEntryClick(val day: Date, val dayTime: DayTime) : HistoryEvent
    data class FoodClicked(val foodId: String) : HistoryEvent
    data class RecipeClicked(val recipeId: String) : HistoryEvent
    data class DetailsClick(val detailsId: String) : HistoryEvent
    data class SelectDate(val day: Date) : HistoryEvent
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: UserDataRepository
) : BaseHistoryViewModel() {
    private val _historyState = MutableStateFlow(HistoryState())
    val historyState = _historyState.asStateFlow()

    private val _events = MutableSharedFlow<HistoryEvent>()
    val events: SharedFlow<HistoryEvent> = _events.asSharedFlow()

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.AddEntryClick -> onAddEntryClick(event.day, event.dayTime)
            is HistoryEvent.FoodClicked -> onFoodClicked(event.foodId)
            is HistoryEvent.RecipeClicked -> onRecipeClicked(event.recipeId)
            is HistoryEvent.DetailsClick -> onDetailsClick(event.detailsId)
            is HistoryEvent.SelectDate -> selectDate(event.day)
        }
    }
    // Die benötigten Parameter sollten über den State bereitgestellt werden, siehe beispiel Profile,
    // außer die Rückgabewerte an das ViewModel, die werden über Events gesendet
    init {
        selectDate(Date())
    }
    override fun onAddEntryClick(day: Date, dayTime: DayTime) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.AddEntryClick(day, dayTime))
        }
    }

    override fun selectDate(day: Date) {
        _historyState.update {
            it.copy(
                selectedDate = day
            )
        }
        displayMealsOfDay(day)
        displayCalorieGoal(day)
    }

    override fun displayCalorieGoal(day: Date) {
        viewModelScope.launch {
            val totalCalories = historyRepository.getCaloriesOfDay(day)
            val goalCalories = userDataRepository.getCalorieGoal()
            _historyState.update {
                it.copy(
                    totalCalories = totalCalories,
                    goalCalories = goalCalories
                )
            }
        }
    }

    //override fun displayNutritionOfDay(day: Date) {}
    override fun displayMealsOfDay(day: Date) {
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

    override fun onFoodClicked(foodId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.FoodClicked(foodId))
        }
    }
    override fun onRecipeClicked(recipeId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.RecipeClicked(recipeId))
        }
    }
    override fun onDetailsClick(detailsId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.DetailsClick(detailsId))
        }
    }
    //override fun onTotalCaloriesClick(totalCalories: Int) {} TODO
}