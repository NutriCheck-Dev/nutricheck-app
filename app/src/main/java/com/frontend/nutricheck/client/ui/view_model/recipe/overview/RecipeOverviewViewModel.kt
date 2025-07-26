package com.frontend.nutricheck.client.ui.view_model.recipe.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeOverviewState(
    val recipe: Recipe = Recipe(),
    val ingredients: List<FoodProduct> = emptyList(),
    val isEditing: Boolean = false
)
sealed interface RecipeOverviewEvent {
    data class ClickDownloadRecipe(val recipe: Recipe) : RecipeOverviewEvent
    data object ClickEditRecipe : RecipeOverviewEvent
    data class ClickDeleteRecipe(val recipe: Recipe) : RecipeOverviewEvent
    data class ClickUploadRecipe(val recipe: Recipe) : RecipeOverviewEvent
}

@HiltViewModel
class RecipeOverviewViewModel @Inject constructor(
    private val recipeRepository: RecipeRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : BaseRecipeOverviewViewModel() {
    private val _recipeOverviewState = MutableStateFlow(RecipeOverviewState())
    val recipeOverviewState: StateFlow<RecipeOverviewState> = _recipeOverviewState.asStateFlow()

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"]) {
        "Missing recipeId in savedStateHandle"
    }

    init {
        viewModelScope.launch {
            recipeRepository.getRecipesWithIngredientsById(recipeId)
                .collect { recipeWithIngredients ->
                    val recipe = recipeWithIngredients.recipe
                    val ingredientsWithProducts = recipeWithIngredients.ingredients
                    val ingredients = ingredientsWithProducts.map { it.foodProduct }
                    _recipeOverviewState.update { it.copy(recipe = recipe, ingredients = ingredients)
                    }
                }
        }
    }


    val _events = MutableSharedFlow<RecipeOverviewEvent>()
    val events: SharedFlow<RecipeOverviewEvent> = _events.asSharedFlow()



    fun onEvent(event: RecipeOverviewEvent) {
        when (event) {
            is RecipeOverviewEvent.ClickDownloadRecipe -> {
                viewModelScope.launch {
                    onDownloadRecipe(event.recipe)
                    _events.emit(RecipeOverviewEvent.ClickDownloadRecipe(event.recipe))
                }
            }
            is RecipeOverviewEvent.ClickEditRecipe -> onEditClicked()
            is RecipeOverviewEvent.ClickDeleteRecipe -> {
                viewModelScope.launch {
                    onDeleteRecipe(event.recipe)
                    _events.emit(RecipeOverviewEvent.ClickDeleteRecipe(event.recipe))
                }
            }
            is RecipeOverviewEvent.ClickUploadRecipe -> {
                viewModelScope.launch {
                    onShareRecipe(event.recipe)
                    _events.emit(RecipeOverviewEvent.ClickUploadRecipe(event.recipe))
                }
            }
        }
    }

    override suspend fun onDownloadRecipe(recipe: Recipe) {
        recipeRepository.insertRecipe(recipe)
    }

    override fun onEditClicked() {
        _recipeOverviewState.update { it.copy(isEditing = !_recipeOverviewState.value.isEditing) }
    }

    override suspend fun onDeleteRecipe(recipe: Recipe) {
        recipeRepository.deleteRecipe(recipe)
    }

    override suspend fun onShareRecipe(recipe: Recipe) {
        //("Not yet implemented")
    }
}