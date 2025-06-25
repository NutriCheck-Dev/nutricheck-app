package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseEditRecipeViewModel : BaseViewModel () {

    abstract fun validate(draft: Recipe): Boolean

    abstract suspend fun persistDraft(draft: Recipe): Result<Unit>

    protected fun updateDraft(newDraft: Recipe) {}

    protected fun resetDraft() {}

    abstract fun addIngredient(ingredientId: String)
    abstract fun removeIngredient(ingredientId: String)
    abstract fun saveChanges()
}