package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseEditRecipeViewModel() : BaseViewModel () {
    abstract fun onTitleChanged(newTitle: String)
    abstract fun onIngredientAdded(foodProduct: FoodProduct)
    abstract fun onIngredientRemovedInSummary(foodProduct: FoodProduct)
    abstract fun onIngredientRemovedInEdit(foodProduct: FoodProduct)
    abstract fun onSaveAddedIngredients()
    abstract fun onDescriptionChanged(newDescription: String)
    abstract suspend fun onCancelEdit()
}