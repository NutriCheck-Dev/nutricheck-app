package com.frontend.nutricheck.client.ui.view_model.recipe.overview

import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseRecipeOverviewViewModel<DRAFT>(
    initialDraft: DRAFT
) : BaseViewModel() {

    protected val _draft = MutableStateFlow(initialDraft)
    val draft: StateFlow<DRAFT> = _draft.asStateFlow()

    abstract fun displayOwnerRecipes()

    abstract fun displayPublicRecipes()

    abstract fun onDraftChanged(newDraft: DRAFT)

    abstract fun onFoodComponentClick(foodId: FoodComponentId)

    abstract fun onAddRecipeClicked()

    abstract fun onEditClicked()

    abstract fun onClickRateRecipe()

    abstract fun onDeleteRecipe()

    abstract fun rateRecipe()

    abstract fun addToMealClick(id: String)

}