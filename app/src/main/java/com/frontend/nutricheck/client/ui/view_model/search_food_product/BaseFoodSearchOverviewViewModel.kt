package com.frontend.nutricheck.client.ui.view_model.search_food_product

import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseFoodSearchOverviewViewModel : BaseViewModel () {
    abstract fun onClickSearchFoodComponent()
    abstract fun onClickRemoveFoodComponent(foodComponent: FoodComponent)

    abstract fun onClickAddFoodComponent(foodComponent: Pair<Double, FoodComponent>)
}