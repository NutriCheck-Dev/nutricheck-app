package com.frontend.nutricheck.client.ui.view_model.add_components

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseAddDialogViewModel : BaseViewModel() {

    private val _close = MutableSharedFlow<Unit>()
    val close: SharedFlow<Unit> = _close.asSharedFlow()

    fun closeDialog() { viewModelScope.launch { _close.emit(Unit) } }

}