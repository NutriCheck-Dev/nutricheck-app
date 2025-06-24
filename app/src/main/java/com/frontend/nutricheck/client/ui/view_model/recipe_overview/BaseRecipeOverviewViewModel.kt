package com.frontend.nutricheck.client.ui.view_model.recipe_list

import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseRecipeOverviewViewModel<DRAFT>(
    initialDraft: DRAFT
) : BaseViewModel () {

    protected val _draft = MutableStateFlow(initialDraft)
    val draft: StateFlow<DRAFT> = _draft.asStateFlow()

    abstract fun displayOwnerRecipes()
    abstract fun displayPublicRecipes()
    abstract fun onDraftChanged(newDraft: DRAFT)

    // Handle recipe item click, e.g., navigate to recipe details
    abstract fun onRecipeClick(foodId: FoodComponentId)

    // Handle add recipe button click, e.g., open recipe selection dialog
    abstract fun onAddRecipeClicked()

    // Handle edit button click, e.g., enable editing mode for the recipe draft
    abstract fun onEditClicked()


}