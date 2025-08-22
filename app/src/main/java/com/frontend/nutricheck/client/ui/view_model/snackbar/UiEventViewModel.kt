package com.frontend.nutricheck.client.ui.view_model.snackbar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for managing UI events related to snackbars.
 */
@HiltViewModel
class UiEventViewModel @Inject constructor(
    val snackbarManager: SnackbarManager
) : ViewModel()