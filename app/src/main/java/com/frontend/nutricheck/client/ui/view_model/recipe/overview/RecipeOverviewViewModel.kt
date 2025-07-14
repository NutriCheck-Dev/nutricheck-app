package com.frontend.nutricheck.client.ui.view_model.recipe.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
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
    val isEditing: Boolean = false
)
sealed interface RecipeOverviewEvent {
    data object ClickEditRecipe : RecipeOverviewEvent
    data class ClickDeleteRecipe(val recipe: Recipe) : RecipeOverviewEvent
    data class ClickShareRecipe(val recipe: Recipe) : RecipeOverviewEvent
}

@HiltViewModel
class RecipeOverviewViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : BaseRecipeOverviewViewModel() {
    private val _recipeOverviewState = MutableStateFlow(RecipeOverviewState())
    val recipeOverviewState: StateFlow<RecipeOverviewState> = _recipeOverviewState.asStateFlow()

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"]) {
        "Missing recipeId in savedStateHandle"
    }
    init {
        viewModelScope.launch {
            recipeRepository.getRecipeById(recipeId)
                .collect { recipe ->
                    _recipeOverviewState.update { it.copy(recipe = recipe)
                    }
                }

        }
    }

    val _events = MutableSharedFlow<RecipeOverviewEvent>()
    val events: SharedFlow<RecipeOverviewEvent> = _events.asSharedFlow()



    fun onEvent(event: RecipeOverviewEvent) {
        when (event) {
            is RecipeOverviewEvent.ClickEditRecipe -> onEditClicked()
            is RecipeOverviewEvent.ClickDeleteRecipe -> {
                viewModelScope.launch {
                    onDeleteRecipe(event.recipe)
                    _events.emit(RecipeOverviewEvent.ClickDeleteRecipe(event.recipe))
                }
            }
            is RecipeOverviewEvent.ClickShareRecipe -> {
                viewModelScope.launch {
                    onShareRecipe(event.recipe)
                    _events.emit(RecipeOverviewEvent.ClickShareRecipe(event.recipe))
                }
            }
        }
    }

    override fun onEditClicked() {
        _recipeOverviewState.update { it.copy(isEditing = true) }
    }

    override suspend fun onDeleteRecipe(recipe: Recipe) {
        recipeRepository.deleteRecipe(recipe)
    }

    override suspend fun onShareRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }


    override fun addToMealClick(recipe: Recipe) {
        TODO("Not yet implemented")
    }

}