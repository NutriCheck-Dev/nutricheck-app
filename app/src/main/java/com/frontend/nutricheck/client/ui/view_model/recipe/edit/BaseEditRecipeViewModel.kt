package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseEditRecipeViewModel() : BaseViewModel () {
    abstract fun onTitleChanged(newTitle: String)
    abstract fun onIngredientAdded(ingredient: Ingredient)
    abstract fun onIngredientRemoved(ingredient: Ingredient)
    abstract fun onDescriptionChanged(newDescription: String)
    abstract fun onCancelEdit()
    abstract fun onSaveRecipe()
}