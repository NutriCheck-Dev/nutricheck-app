package com.frontend.nutricheck.client.ui.view_model.add_components

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseAddMealViewModel : BaseViewModel() {

    abstract fun onAddFoodProductClicked()
    abstract fun saveMeal()
    abstract fun saveAsRecipe()
    abstract fun displayMealDetails()
    abstract fun onFoodComponentClick(foodId: String)
    abstract fun onMyRecipesClick()
    abstract fun onAddClick()
}