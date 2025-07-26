package com.frontend.nutricheck.client.ui.view_model.add_components

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.HistoryDay
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepositoryImpl
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
import java.util.Date

data class AddMealState(
    val foodComponents: List<FoodComponent> = emptyList(),
    val historyDay: HistoryDay? = null,
    val mealTime: DayTime? = null,
    val currentSegment: String = "All",
    val selectedMeal: Meal? = null
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
    private val historyRepository: HistoryRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val historyDateMillis: Long? = savedStateHandle.get<Long>("historyDate")
        .takeIf { it != null && it > 0L }
    private val historyDate: Date? = historyDateMillis?.let { Date(it) }

    private val _addMealState = MutableStateFlow(AddMealState())
    val createRecipeState = _addMealState.asStateFlow()

    init {
        historyDate?.let { date ->
            viewModelScope.launch {
                historyRepository
                    .getHistoryByDate(date)
                    .collect { historyDay ->
                        _addMealState.update {
                            it.copy(historyDay = historyDay)
                        }
                    }
            }
        }
    }

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