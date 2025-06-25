package com.frontend.nutricheck.client.ui.view_model.food

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseFoodOverviewViewModel<DRAFT>(
    initialDraft: DRAFT
) :BaseViewModel() {

    protected val _draft = MutableStateFlow(initialDraft)
    val draft: StateFlow<DRAFT> = _draft.asStateFlow()

    abstract fun addToMealClick(id: String)

    abstract fun onEditClick()
}