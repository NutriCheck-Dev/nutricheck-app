package com.frontend.nutricheck.client.ui.view_model.edit_recipe

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseEditRecipeViewModel<DRAFT>(
    initialDraft: DRAFT
) : BaseViewModel () {
    protected val _draft = MutableStateFlow(initialDraft)
    val draft: StateFlow<DRAFT> = _draft.asStateFlow()

    abstract fun validate(draft: DRAFT): Boolean

    abstract suspend fun persistDraft(draft: DRAFT): Result<Unit>

    protected fun updateDraft(newDraft: DRAFT) {}

    protected fun resetDraft() {
        //_draft.value = _draft.value This should reset to the initial state, but we need a way to store the initial state.
    }
    abstract fun onClickEditRecipe() 
    abstract fun onClickUploadRecipe()
    abstract fun onDeleteRecipe()
    abstract fun displayEditMenu() 

    abstract fun addIngredient(ingredientId: String)
    abstract fun removeIngredient(ingredientId: String)
    abstract fun saveChanges()
    abstract fun onClickDiscardDraft()

}