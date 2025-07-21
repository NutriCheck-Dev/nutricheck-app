package com.frontend.nutricheck.client.ui.view_model.search_food_product

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseFoodSearchOverviewViewModel : BaseViewModel () {

    private val _close = MutableSharedFlow<Unit>()
    val close: SharedFlow<Unit> = _close.asSharedFlow()

    fun closeDialog() { viewModelScope.launch { _close.emit(Unit) } }

    abstract fun onClickSearchFoodComponent()
    abstract fun onClickAddFoodComponent(foodComponent: FoodComponent)
    abstract fun onClickRemoveFoodComponent(foodComponent: FoodComponent)
    abstract fun onFoodClick()
    abstract fun onRecipeClick()

}