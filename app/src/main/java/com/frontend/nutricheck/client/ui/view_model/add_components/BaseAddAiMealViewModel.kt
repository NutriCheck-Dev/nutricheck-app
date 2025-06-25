package com.frontend.nutricheck.client.ui.view_model.add_components

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseAddAiMealViewModel : BaseViewModel() {

    abstract fun retakeFoto()
    abstract fun saveMeal()
    abstract fun saveAsRecipe()
    abstract fun getMealDetails()
}