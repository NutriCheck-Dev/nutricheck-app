package com.frontend.nutricheck.client.ui.view_model.search_food_product

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseFoodSearchOverviewViewModel : BaseViewModel () {

    protected val _close = MutableSharedFlow<Unit>()
    val close: SharedFlow<Unit> = _close.asSharedFlow()

    fun closeDialog() { viewModelScope.launch { _close.emit(Unit) } }

    abstract fun onClickSearchFoodProduct()
    abstract fun onClickAddFoodProduct()
    abstract fun onFoodClick()
    abstract fun onRecipeClick()
    abstract fun onMyRecipesClick()
    abstract fun onOnlieRecipesClick()

}