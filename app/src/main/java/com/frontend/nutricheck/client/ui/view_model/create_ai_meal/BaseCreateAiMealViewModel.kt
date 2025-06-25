package com.frontend.nutricheck.client.ui.view_model.create_ai_meal

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseCreateAiMealViewModel : BaseViewModel() {

    abstract fun takeFoto()
    abstract fun displayFoodSuggestions()
    abstract fun selectFoodSuggestion()
    abstract fun saveMeal()
    abstract fun saveAsRecipe()
    abstract fun displayMealDetails()
}