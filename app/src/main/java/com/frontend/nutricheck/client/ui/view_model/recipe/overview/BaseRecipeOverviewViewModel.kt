package com.frontend.nutricheck.client.ui.view_model.recipe.overview

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseRecipeOverviewViewModel<DRAFT>(
    initialDraft: DRAFT
) : BaseViewModel() {

    private val _draft = MutableStateFlow(initialDraft)
    val draft: StateFlow<DRAFT> = _draft.asStateFlow()

    abstract fun onDraftChanged(newDraft: DRAFT)

    abstract fun onFoodComponentClick(foodId: String)

    abstract fun onAddRecipeClicked()

    abstract fun onEditClicked()

    abstract fun onDeleteRecipe()

    abstract fun addToMealClick(id: String)

}