package com.frontend.nutricheck.client.ui.view_model.recipe.create

import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


abstract class BaseCreateRecipeViewModel: BaseViewModel () {

    abstract fun validate(draft: Recipe): Boolean

    protected abstract suspend fun persistDraft(draft: Recipe): Result<Unit>

    fun save(onSuccess: () -> Unit = {}) {}

    abstract fun onClickAddIngredient()
    abstract fun onClickRemoveIngredient(ingredientId: String)  
    abstract fun onClickSaveRecipe()
    abstract fun onClickDiscardDraft()
    abstract fun onIngredientClick()
   
}