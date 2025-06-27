package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseEditRecipeViewModel : BaseViewModel () {

    abstract fun validate(draft: Recipe): Boolean

    abstract suspend fun persistDraft(draft: Recipe): Result<Unit>

    protected fun updateDraft(newDraft: Recipe) {}

    protected fun resetDraft() {}

    abstract fun addIngredient(ingredientId: String)
    abstract fun removeIngredient(ingredientId: String)
    abstract fun saveChanges()
}