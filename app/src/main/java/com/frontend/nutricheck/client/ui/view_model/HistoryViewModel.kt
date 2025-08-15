package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Data class representing the state of the History screen
 */
data class HistoryState(
    val selectedDate: Date = Date(),
    val nutritionOfDay: Map<String, Int> = emptyMap(),
    val mealsGrouped: Map<DayTime, List<Meal>> = emptyMap(),
    val mealId: String = "",
    val foodId: String = "",
    val recipeId: String = "",
    val dayTime: DayTime = DayTime.BREAKFAST,
    val totalCalories: Int = 0,
    val goalCalories: Int = 0,
    val switched: Boolean = false
)

/**
 * Sealed interface defining all possible events that can occur in the History screen
 */
sealed interface HistoryEvent {
    data class AddEntryClick(val day: Date, val dayTime: DayTime) : HistoryEvent
    data class FoodClicked(val mealId: String, val foodId: String) : HistoryEvent
    data class RecipeClicked(val mealId: String, val recipeId: String) : HistoryEvent
    data class RemoveMealItem(val mealItem: MealItem) : HistoryEvent
    data class DetailsClick(val detailsId: String) : HistoryEvent
    data class SelectDate(val day: Date) : HistoryEvent
}

/**
 * ViewModel for managing the History screen state and business logic
 *
 * @param historyRepository Repository for historical meal and nutrition data
 * @param userDataRepository Repository for user-specific data like calorie goals
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userDataRepository: UserDataRepository
) : BaseViewModel() {

    private val _historyState = MutableStateFlow(HistoryState())
    val historyState = _historyState.asStateFlow()

    private val _events = MutableSharedFlow<HistoryEvent>()
    val events: SharedFlow<HistoryEvent> = _events.asSharedFlow()

    /**
     * Handles incoming events and delegates to appropriate handler methods
     *
     * @param event The event to process
     */
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
        // Initialize with current date
        selectDate(Date())
    }

    /**
     * Handles the add entry button click
     *
     * @param day The date for the new entry
     * @param dayTime The time of day for the new entry
     */
    fun onAddEntryClick(day: Date, dayTime: DayTime) {
        viewModelScope.launch {
            _historyState.update {
                it.copy(
                    selectedDate = day,
                    dayTime = dayTime
                )
            }
            _events.emit(HistoryEvent.AddEntryClick(day, dayTime))
        }
    }

    /**
     * Observes meals for the currently selected date
     * Updates the state when meals change, grouping them by day time
     */
    private fun observeMeals() {
        viewModelScope.launch {
            historyState
                .map { it.selectedDate }
                .distinctUntilChanged() // Only react to date changes
                .flatMapLatest { selectedDate ->
                    historyRepository.observeMealsForDay(selectedDate)
                }
                .collect { meals ->
                    _historyState.update {
                        it.copy(mealsGrouped = meals.groupBy { meal -> meal.dayTime })
                    }
                }
        }
    }

    /**
     * Observes calorie information for the currently selected date
     */
    private fun observeCalories() {
        viewModelScope.launch {
            historyState
                .map { it.selectedDate }
                .distinctUntilChanged() // Only react to date changes
                .flatMapLatest { selectedDate ->
                    historyRepository.observeCaloriesOfDay(selectedDate)
                }
                .collect { totalCalories ->
                    val goalCalories = userDataRepository.getDailyCalorieGoal()
                    _historyState.update {
                        it.copy(
                            totalCalories = totalCalories,
                            goalCalories = goalCalories
                        )
                    }
                }
        }
    }

    /**
     * Selects a new date and triggers data observation for that date
     *
     * @param day The date to select and observe
     */
    fun selectDate(day: Date) {
        _historyState.update {
            it.copy(selectedDate = day)
        }
        observeMeals()
        observeCalories()
    }

    /**
     * Removes a meal item from the user's history
     *
     * @param mealItem The meal item to remove
     */
    fun onRemoveMealItem(mealItem: MealItem) {
        viewModelScope.launch {
            historyRepository.removeMealItem(mealItem)
        }
    }

    /**
     * Handles food item click events
     *
     * @param mealId ID of the meal containing the food
     * @param foodId ID of the clicked food item
     */
    fun onFoodClicked(mealId: String, foodId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.FoodClicked(mealId, foodId))
        }
    }

    /**
     * Handles recipe click events
     *
     * @param mealId ID of the meal containing the recipe
     * @param recipeId ID of the clicked recipe
     */
    fun onRecipeClicked(mealId: String, recipeId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.RecipeClicked(mealId, recipeId))
        }
    }

    /**
     * Handles details button click events
     *
     * @param detailsId ID of the item to show details for
     */
    fun onDetailsClick(detailsId: String) {
        viewModelScope.launch {
            _events.emit(HistoryEvent.DetailsClick(detailsId))
        }
    }
}