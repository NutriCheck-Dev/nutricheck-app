package com.frontend.nutricheck.client.ui.view_model.recipe.create

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel


abstract class BaseCreateRecipeViewModel: BaseViewModel () {

    abstract fun onTitleAdded(newTitle: String)
    abstract fun onIngredientAdded(foodProduct: FoodProduct)
    abstract fun onIngredientRemovedInSummary(foodProduct: FoodProduct)
    abstract fun onIngredientRemovedInCreation(foodProduct: FoodProduct)
    abstract fun onSaveAddedIngredients()
    abstract fun onDescriptionAdded(newDescription: String)
    abstract fun onCancelCreation()
   
}