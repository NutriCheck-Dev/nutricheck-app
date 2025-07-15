package com.frontend.nutricheck.client.ui.view_model.add_components

import com.frontend.nutricheck.client.model.data_sources.data.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class AddAiMealState(
    val foodSuggestions: List<String> = emptyList(),
    val selectedFoodSuggestion: String? = null,
    val meal: Meal = Meal()
)

sealed interface AddAiMealEvent {
    data object TakeFoto : AddAiMealEvent
    data object SaveMeal : AddAiMealEvent
    data object SaveAsRecipe : AddAiMealEvent
    data object DisplayMealDetails : AddAiMealEvent
}

@HiltViewModel
class AddAiMealViewModel @Inject constructor() : BaseAddAiMealViewModel() {

    private val _addAiMealState = MutableStateFlow(AddAiMealState())
    val addAiMealState = _addAiMealState.asStateFlow()

    private val _events = MutableSharedFlow<AddAiMealEvent>()
    val events: SharedFlow<AddAiMealEvent> = _events.asSharedFlow()

    fun onEvent(event: AddAiMealEvent) {}

    override fun takeFoto() {
        TODO("Not yet implemented")
    }

    override fun saveMeal() {
        TODO("Not yet implemented")
    }

    override fun saveAsRecipe() {
        TODO("Not yet implemented")
    }

    override fun getMealDetails() {
        TODO("Not yet implemented")
    }
}