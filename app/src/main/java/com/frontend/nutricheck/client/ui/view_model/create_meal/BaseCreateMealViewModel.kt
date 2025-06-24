package com.frontend.nutricheck.client.ui.view_model.create_meal

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseCreateMealViewModel : BaseViewModel() {

    abstract fun onAddFoodProductClicked()
    abstract fun saveMeal()
    abstract fun saveAsRecipe()
    abstract fun displayMealDetails()
    abstract fun displayFoodProductHistory()
    abstract fun onFoodProductHistoryItemClicked(foodId: String)
    abstract fun onMyRecipesClick()
}