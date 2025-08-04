package com.frontend.nutricheck.client.ui.view_model.add_components

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddMealState(
    val foodComponents: List<FoodComponent> = emptyList(),
    val mealTime: DayTime? = null,
    val currentSegment: String = "All",
    val selectedMeal: MealEntity? = null
)

sealed interface AddMealEvent {
    data class SelectMealTime(val mealTime: DayTime) : AddMealEvent
    data class SaveMeal(val components: List<FoodComponent>) : AddMealEvent
    data object LogMealClick : AddMealEvent
    data object ScanMealClick : AddMealEvent
    data object CreateRecipeClick : AddMealEvent
}

@HiltViewModel
class AddMealViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _addMealState = MutableStateFlow(AddMealState())

    private val _events = MutableSharedFlow<AddMealEvent>()
    val events: SharedFlow<AddMealEvent> = _events.asSharedFlow()


    fun onEvent(event: AddMealEvent) {
        when (event) {
            is AddMealEvent.SelectMealTime -> selectMealTime(event.mealTime)
            is AddMealEvent.SaveMeal -> saveMeal(event.components)
            AddMealEvent.LogMealClick -> onLogMealClick()
            AddMealEvent.ScanMealClick -> onScanMealClick()
            AddMealEvent.CreateRecipeClick -> onCreateRecipeClick()
        }
    }
    private fun saveMeal(components: List<FoodComponent>) {
        viewModelScope.launch {
            _events.emit(AddMealEvent.SaveMeal(components))
        }
    }

    private fun selectMealTime(mealTime: DayTime) {
        _addMealState.update { it.copy(mealTime = mealTime) }
    }
    private fun onLogMealClick() =
        emitEvent(AddMealEvent.LogMealClick)

    private fun onScanMealClick() =
        emitEvent(AddMealEvent.ScanMealClick)

    private fun onCreateRecipeClick() =
        emitEvent(AddMealEvent.CreateRecipeClick)


    private fun emitEvent(event: AddMealEvent) = viewModelScope.launch { _events.emit(event) }
}