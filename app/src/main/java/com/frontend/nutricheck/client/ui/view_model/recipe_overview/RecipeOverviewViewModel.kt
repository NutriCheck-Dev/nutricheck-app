package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.BaseRecipeRepository
import com.frontend.nutricheck.client.ui.view_model.recipe_list.BaseRecipeOverviewViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RecipeOverviewViewModel(
    private val repository: BaseRecipeRepository
): BaseRecipeOverviewViewModel<Recipe>(
    initialDraft = Recipe()
) {
    val title = draft.map { it.name }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val ingredients = draft.map { it.ingredients }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val description = draft.map { it.description }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val energyInKcal = draft.map { it.energyInKcal }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    override fun onDraftChanged(newDraft: Recipe) {
        TODO("Not yet implemented")
    }

    override fun onFoodClicked(foodId: FoodComponentId) {
        TODO("Not yet implemented")
    }

    override fun onAddFoodClicked() {
        TODO("Not yet implemented")
    }

    override fun onSaveClicked() {
        TODO("Not yet implemented")
    }

    override fun onCancelClicked() {
        TODO("Not yet implemented")
    }

    override fun onDeleteClicked() {
        TODO("Not yet implemented")
    }

    override fun onShareClicked() {
        TODO("Not yet implemented")
    }

    override fun onEditClicked() {
        TODO("Not yet implemented")
    }

    override fun onReportClicked() {
        TODO("Not yet implemented")
    }
}