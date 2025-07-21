package com.frontend.nutricheck.client.ui.view_model.history

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel
import java.util.Date

abstract class BaseHistoryViewModel : BaseViewModel() {

    abstract fun onAddEntryClick(day: Date)
    abstract fun selectDate(date: Date)
    abstract fun displayCalorieGoal()
    abstract fun displayNutritionOfDay(day: Date)
    abstract fun displayMealsOfDay(day: Date)
    abstract fun onFoodClicked(foodId: String)
    abstract fun onDetailsClick(detailsId: String)
    abstract fun onTotalCaloriesClick(totalCalories: Int)
    abstract fun onSwitchClick(isSwitched: Boolean)
    fun onRecipeClick() = RecipePageViewModel.onRecipeClick()


}

private fun RecipePageViewModel.Companion.onRecipeClick(): Unit {
//TODO
}
