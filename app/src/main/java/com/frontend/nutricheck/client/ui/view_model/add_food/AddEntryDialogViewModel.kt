package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.ui.view_model.add_food.BaseAddDialogViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddEntryDialogViewModel : BaseAddDialogViewModel() {

    sealed interface DialogEvent {
        object AddMeal : DialogEvent
        object ScanFood : DialogEvent
        object AddRecipe : DialogEvent
    }


    private val _events = MutableSharedFlow<DialogEvent>()
    val events: SharedFlow<DialogEvent> = _events.asSharedFlow()

    fun onAddMealClick() { emitEvent(DialogEvent.AddMeal) }

    fun onScanFoodClick() { emitEvent(DialogEvent.ScanFood) }

    fun onAddRecipeClick() { emitEvent(DialogEvent.AddRecipe) }

    fun onDismiss() {}

    private fun emitEvent(event: DialogEvent) = viewModelScope.launch { _events.emit(event) }
}