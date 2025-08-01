package com.frontend.nutricheck.client.ui.view_model.history

import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import java.util.Date

abstract class BaseHistoryViewModel : BaseViewModel() {

    abstract fun onAddEntryClick(day: Date, dayTime: DayTime)
    abstract fun selectDate(day: Date)
    abstract fun displayMealsOfDay(day: Date)
    abstract fun displayCalorieGoal(day: Date)
    abstract fun onFoodClicked(mealId:String, foodId: String)
    abstract fun onDetailsClick(detailsId: String)
    abstract fun onRecipeClicked(mealId: String, recipeId: String)

}


