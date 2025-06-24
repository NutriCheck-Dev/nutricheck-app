package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.BaseRecipeRepository
import com.frontend.nutricheck.client.ui.view_model.recipe_list.BaseRecipeOverviewViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RecipeOverviewViewModel  : BaseRecipeOverviewViewModel<Recipe>(
    initialDraft = Recipe()
) {
    private val repository: BaseRecipeRepository<Recipe> = TODO("Not yet implemented")

    val title = draft.map { it.name }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val ingredients = draft.map { it.ingredients }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val description = draft.map { it.description }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val energyInKcal = draft.map { it.calories }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    override fun onDraftChanged(newDraft: Recipe) {
        TODO("Not yet implemented")
    }

    override fun onFoodClicked(foodId: String) {
        TODO("Not yet implemented")
    }

    override fun onAddFoodClicked() {
        TODO("Not yet implemented")
    }

    override fun onEditClicked() {
        TODO("Not yet implemented")
    }

    override fun displayOwnerRecipes() {
        // Implementation to display recipes owned by the user
    }
    override fun displayPublicRecipes() {
        // Implementation to display public recipes
    }
}