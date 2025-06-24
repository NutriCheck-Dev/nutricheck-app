package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.ui.view_model.history.BaseHistoryViewModel

class HistoryViewModel : BaseHistoryViewModel() {
    override fun onAddEntryClick() {}

    override fun selectDate(date: String) {}
    override fun displayNutritionOfDay(day: String) {}
    override fun displayMealsOfDay(day: String) {}
}