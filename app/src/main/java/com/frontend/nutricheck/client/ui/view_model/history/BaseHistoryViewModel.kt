package com.frontend.nutricheck.client.ui.view_model.history

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel

abstract class BaseHistoryViewModel : BaseViewModel() {

    abstract fun onAddEntryClick()
    abstract fun selectDate(date: String)
    abstract fun displayNutritionOfDay(day: String)
    abstract fun displayMealsOfDay(day: String)
    abstract fun onFoodClicked(foodId: String)
    abstract fun onDetailsClick(detailsId: String)
    abstract fun onTotalCaloriesClick(totalCalories: Int)
    abstract fun onSwitchClick(isSwitched: Boolean)
    fun onRecipeClick() = RecipePageViewModel.onRecipeClick()


}

private fun RecipePageViewModel.Companion.onRecipeClick(): Unit {
//TODO
}
