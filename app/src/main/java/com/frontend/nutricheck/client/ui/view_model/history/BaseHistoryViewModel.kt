package com.frontend.nutricheck.client.ui.view_model.history

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.RecipeListViewModel

abstract class BaseHistoryViewModel : BaseViewModel () {

    abstract fun onAddEntryClick()
    abstract fun selectDate(date: String)
    abstract fun displayNutritionOfDay(day: String)
    abstract fun displayMealsOfDay(day: String)
    abstract fun onFoodClicked()


    fun onRecipeClick() = RecipeListViewModel.onRecipeClick()

}

private fun RecipeListViewModel.Companion.onRecipeClick(): Unit {
//TODO
}
