package com.frontend.nutricheck.client.ui.view_model.create_meal

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseCreateMealViewModel : BaseViewModel() {

    abstract fun onAddFoodClicked()
    abstract fun saveMeal()
    abstract fun saveAsRecipe()
    abstract fun displayMealDetails()
    abstract fun displayFoodHistory()
    abstract fun onFoodHistoryItemClicked(foodId: String)
    abstract fun onMyRecipesClick()
}