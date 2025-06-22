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

    abstract fun onDraftChanged(newDraft: DRAFT)

    // Handle food item click, e.g., navigate to food details
    abstract fun onFoodClicked(foodId: FoodComponentId)

    // Handle add food button click, e.g., open food selection dialog
    abstract fun onAddFoodClicked()

    // Handle save button click, e.g., save the recipe draft
    abstract fun onSaveClicked()

    // Handle cancel button click, e.g., discard changes and navigate back
    abstract fun onCancelClicked()

    // Handle delete button click, e.g., delete the recipe draft
    abstract fun onDeleteClicked()

    // Handle share button click, e.g., share the recipe draft
    abstract fun onShareClicked()

    // Handle edit button click, e.g., enable editing mode for the recipe draft
    abstract fun onEditClicked()

    // Handle report button click, e.g., report an issue with the recipe draft
    abstract fun onReportClicked()

}