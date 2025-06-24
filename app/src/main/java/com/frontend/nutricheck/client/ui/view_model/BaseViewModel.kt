package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


abstract class BaseViewModel : ViewModel() {

    sealed interface UiState {
        object Ready : UiState
        object Loading : UiState
        data class Error(val message: String) : UiState
    }

    protected val _uiState = MutableStateFlow<UiState>(UiState.Ready)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    protected fun setLoading() { _uiState.value = UiState.Loading }
    protected fun setReady() { _uiState.value = UiState.Ready }
    protected fun setError(message: String) { _uiState.value = UiState.Error(message) }
}