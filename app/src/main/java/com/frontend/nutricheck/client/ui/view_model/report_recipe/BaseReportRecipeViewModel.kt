package com.frontend.nutricheck.client.ui.view_model.report_recipe

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseReportRecipeViewModel : BaseViewModel () {

    sealed interface UiState {
        object Ready : UiState
        data class Error(val message: String) : UiState
    }

    protected val _close = MutableSharedFlow<Unit>()
    val close: SharedFlow<Unit> = _close.asSharedFlow()

    fun closeDialog() {viewModelScope.launch { _close.emit(Unit) }}

    abstract fun onClickMessage() 
    abstract fun onClickSendReport()
    abstract fun onClickCancel()
    
}