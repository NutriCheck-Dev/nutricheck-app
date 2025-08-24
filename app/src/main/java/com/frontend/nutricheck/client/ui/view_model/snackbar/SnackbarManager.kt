package com.frontend.nutricheck.client.ui.view_model.snackbar

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A manager for displaying snackbars in the application.
 */
@Singleton
class SnackbarManager @Inject constructor() {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    fun show(message: String) {
        _messages.tryEmit(message)
    }
}