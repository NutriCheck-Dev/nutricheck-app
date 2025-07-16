package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseEditRecipeViewModel<DRAFT>(
    initialDraft: DRAFT
) : BaseViewModel () {
    private val _draft = MutableStateFlow(initialDraft)
    val draft:  StateFlow<DRAFT> = _draft.asStateFlow()

    abstract fun updateDraft(newDraft: DRAFT)
    abstract fun resetDraft()
    abstract fun addIngredient(ingredientId: String)
    abstract fun removeIngredient(ingredientId: String)
}