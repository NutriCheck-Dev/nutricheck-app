package com.frontend.nutricheck.client.ui.view_model.recipe.create

import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel


abstract class BaseCreateRecipeViewModel: BaseViewModel () {

    abstract fun onTitleAdded(newTitle: String)
    abstract fun onIngredientAdded(ingredient: Ingredient)
    abstract fun onIngredientRemoved(ingredient: Ingredient)
    abstract fun onDescriptionAdded(newDescription: String)
    abstract fun onCancelCreation()
    abstract fun onSaveRecipe()
   
}