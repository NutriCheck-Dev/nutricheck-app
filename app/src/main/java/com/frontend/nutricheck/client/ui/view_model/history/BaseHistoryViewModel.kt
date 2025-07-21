package com.frontend.nutricheck.client.ui.view_model.history

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseHistoryViewModel : BaseViewModel() {

    abstract fun onAddEntryClick()
    abstract fun selectDate(date: String)
    abstract fun displayNutritionOfDay(day: String)
    abstract fun displayMealsOfDay(day: String)
    abstract fun onFoodClicked()
    abstract fun onDetailsClick()
    abstract fun onTotalCaloriesClick()
    abstract fun onSwitchClick()


}


