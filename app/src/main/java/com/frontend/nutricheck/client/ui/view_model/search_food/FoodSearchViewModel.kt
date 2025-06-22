package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.ui.view_model.search_food.BaseSearchFoodViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FoodSearchViewModel : BaseSearchFoodViewModel() {

    sealed interface DialogEvent {
        object Entry : DialogEvent
    }

    private val _events = MutableSharedFlow<DialogEvent>()
    val events: SharedFlow<DialogEvent> = _events.asSharedFlow()

    fun onSearchClick() { emitEvent(DialogEvent.Entry) }

    private fun emitEvent(event: DialogEvent) = viewModelScope.launch { _events.emit(event)}
}