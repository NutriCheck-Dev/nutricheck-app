package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseEditRecipeViewModel() : BaseViewModel () {
    abstract fun onTitleChanged(newTitle: String)
    abstract fun onIngredientAdded(ingredient: FoodComponent)
    abstract fun onIngredientRemovedInSummary(ingredient: FoodComponent)
    abstract fun onIngredientRemovedInEdit(ingredient: FoodComponent)
    abstract fun onSaveAddedIngredients()
    abstract fun onDescriptionChanged(newDescription: String)
    abstract fun onCancelEdit()
    abstract fun onSaveRecipe()
}