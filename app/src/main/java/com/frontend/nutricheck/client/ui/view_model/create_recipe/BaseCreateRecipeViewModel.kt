package com.frontend.nutricheck.client.ui.view_model.create_recipe

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseCreateRecipeViewModel<DRAFT>(
    initialDraft: DRAFT
) : BaseViewModel () {

    protected val _draft = MutableStateFlow(initialDraft)
    val draft: StateFlow<DRAFT> = _draft.asStateFlow()

    abstract fun validate(draft: DRAFT): Boolean

    protected abstract suspend fun persistDraft(draft: DRAFT): Result<Unit>

    fun save(onSuccess: () -> Unit = {}) {}

    abstract fun onClickAddIngredient()
    abstract fun onClickRemoveIngredient(ingredientId: String)  
    abstract fun onClickSaveRecipe()
    abstract fun onClickDiscardDraft()
    
}