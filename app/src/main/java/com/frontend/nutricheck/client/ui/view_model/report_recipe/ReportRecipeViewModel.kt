package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.ui.view_model.report_recipe.BaseReportRecipeViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ReportRecipeViewModel : BaseReportRecipeViewModel() {

    sealed interface DialogEvent {
        object TextInput : DialogEvent
        object SendReport : DialogEvent
    }

    private val _events = MutableSharedFlow<DialogEvent>()
    val events: SharedFlow<DialogEvent> = _events.asSharedFlow()

    fun onTextInput() { emitEvent(DialogEvent.TextInput) }
    fun onSendReport() { emitEvent(DialogEvent.SendReport) }

    private fun emitEvent(event: DialogEvent) = viewModelScope.launch { _events.emit(event) }

    override fun onClickMessage() {
        // Logic to handle message click
    }

    override fun onClickSendReport() {
        // Logic to handle send report click
    }

    override fun onClickCancel() {
        // Logic to handle cancel click
    }
}