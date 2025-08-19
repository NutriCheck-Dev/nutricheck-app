package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UiEventViewModel @Inject constructor(
    val snackbarManager: SnackbarManager
) : ViewModel()