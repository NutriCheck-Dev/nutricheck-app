package com.frontend.nutricheck.client.ui.view_model.recipe.report

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class ReportRecipeState(
    val message: String = "",
    val isSending: Boolean = false,
    val isOpen: Boolean = false
)

sealed interface ReportRecipeEvent {
    data object TextInput : ReportRecipeEvent
    data object SendReport : ReportRecipeEvent
}

@HiltViewModel
class ReportRecipeViewModel @Inject constructor(
    initialState: ReportRecipeState = ReportRecipeState()
) : BaseReportRecipeViewModel() {

    private val _events = MutableSharedFlow<ReportRecipeEvent>()
    val events: SharedFlow<ReportRecipeEvent> = _events.asSharedFlow()

    fun onTextInput() { emitEvent(ReportRecipeEvent.TextInput) }
    fun onSendReport() { emitEvent(ReportRecipeEvent.SendReport) }

    private fun emitEvent(event: ReportRecipeEvent) = viewModelScope.launch { _events.emit(event) }

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