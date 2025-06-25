package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.BaseRecipeOverviewViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface RecipeOverviewEvent {
    data class RecipeClick(val recipeId: String) : RecipeOverviewEvent
    object AddRecipe : RecipeOverviewEvent
    object EditRecipe : RecipeOverviewEvent
    object RateRecipe : RecipeOverviewEvent
    object DisplayOwnerRecipes : RecipeOverviewEvent
    object DisplayPublicRecipes : RecipeOverviewEvent
}

@HiltViewModel
class RecipeOverviewViewModel @Inject constructor() : BaseRecipeOverviewViewModel<Recipe>(
    initialDraft = Recipe()
) {

    val title = draft.map { it.name }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val ingredients = draft.map { it.ingredients }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val description = draft.map { it.description }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val calories = draft.map { it.calories }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val fats = draft.map { it.fat }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val carbs = draft.map { it.carbs }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val proteins = draft.map { it.protein }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val _events = MutableSharedFlow<RecipeOverviewEvent>()
    val events: SharedFlow<RecipeOverviewEvent> = _events.asSharedFlow()


    fun onEvent(event: RecipeOverviewEvent) {}


    override fun onDraftChanged(newDraft: Recipe) {
        TODO("Not yet implemented")
    }

    override fun onEditClicked() {
        TODO("Not yet implemented")
    }

    override fun onClickRateRecipe() {
        TODO("Not yet implemented")
    }

    override fun onDeleteRecipe() {
        TODO("Not yet implemented")
    }

    override fun rateRecipe() {
        TODO("Not yet implemented")
    }

    override fun addToMealClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun displayOwnerRecipes() {
        // Implementation to display recipes owned by the user
    }
    override fun displayPublicRecipes() {
        // Implementation to display public recipes
    }

    override fun onFoodComponentClick(foodId: FoodComponentId) {
        TODO("Not yet implemented")
    }

    override fun onAddRecipeClicked() {
        TODO("Not yet implemented")
    }
}