package com.frontend.nutricheck.client.ui.view_model.add_components

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface AddDialogEvent {
    data object AddMeal : AddDialogEvent
    data object ScanFood : AddDialogEvent
    data object AddRecipe : AddDialogEvent
}

@HiltViewModel
class AddDialogViewModel @Inject constructor() : BaseAddDialogViewModel() {

    private val _events = MutableSharedFlow<AddDialogEvent>()
    val events: SharedFlow<AddDialogEvent> = _events.asSharedFlow()

    fun onEvent(event: AddDialogEvent) {
        when (event) {
            is AddDialogEvent.AddMeal -> onAddMealClick()
            is AddDialogEvent.ScanFood -> onScanFoodClick()
            is AddDialogEvent.AddRecipe -> onAddRecipeClick()
        }
    }

    private fun onAddMealClick() { emitEvent(AddDialogEvent.AddMeal) }

    private fun onScanFoodClick() { emitEvent(AddDialogEvent.ScanFood) }

    private fun onAddRecipeClick() { emitEvent(AddDialogEvent.AddRecipe) }

    private fun emitEvent(event: AddDialogEvent) = viewModelScope.launch { _events.emit(event) }
}