package com.frontend.nutricheck.client.ui.view_model.food

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseFoodOverviewViewModel() :BaseViewModel() {

    abstract fun onSaveAndAddClick(): FoodProduct
    abstract fun onBackClick()
    abstract fun onServingsChanged(servings: Int)
    abstract fun onServingSizeChanged(servingSize: ServingSize)
}