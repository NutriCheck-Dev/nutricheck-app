package com.frontend.nutricheck.client.ui.view_model.recipe.page

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipePageState(
    val myRecipes: List<Recipe> = emptyList(),
    val onlineRecipes: List<Recipe> = emptyList(),
    val selectedTab: Int = 0,
)

sealed interface RecipePageEvent {
    data object ClickAddRecipe : RecipePageEvent
    data object ClickRecipe : RecipePageEvent
    data object ClickDetails : RecipePageEvent
    data object ClickMyRecipes : RecipePageEvent
    data object ClickOnlineRecipes : RecipePageEvent
    data class ClickSaveRecipe(val recipe: Recipe) : RecipePageEvent
    data class ClickDeleteRecipe(val recipe: Recipe) : RecipePageEvent
    data class ShowSnackbar(val message: String) : RecipePageEvent
}

@HiltViewModel
class RecipePageViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : BaseRecipePageViewModel() {


    private val _recipePageState = MutableStateFlow(RecipePageState())
    init {
        viewModelScope.launch {
            combine(
                recipeRepository.getMyRecipes(),
            recipeRepository.getOnlineRecipes()
            ) { myRecipes, onlineRecipes ->
                RecipePageState(myRecipes = myRecipes, onlineRecipes = onlineRecipes)
            }.collect { _recipePageState.value = it}
        }
    }
    val recipePageState: StateFlow<RecipePageState> = _recipePageState.asStateFlow()

    private val _events = MutableSharedFlow<RecipePageEvent>()
    val events: SharedFlow<RecipePageEvent> = _events.asSharedFlow()

    fun onEvent(event: RecipePageEvent) {
        when(event) {
            is RecipePageEvent.ClickMyRecipes -> onMyRecipesClick()
            is RecipePageEvent.ClickOnlineRecipes -> onOnlineRecipesClick()
            is RecipePageEvent.ClickSaveRecipe -> viewModelScope.launch { onSaveRecipeClick(event.recipe) }
            is RecipePageEvent.ClickDeleteRecipe -> viewModelScope.launch { onDeleteRecipeClick(event.recipe) }
            is RecipePageEvent.ShowSnackbar -> emitEvent(RecipePageEvent.ShowSnackbar(event.message))
            else -> { /* Don´t handle other here */ }
        }
    }

    override fun onMyRecipesClick() {
        _recipePageState.update { it.copy(selectedTab = 0) }
        emitEvent(RecipePageEvent.ClickMyRecipes)
    }

    override fun onOnlineRecipesClick() {
        _recipePageState.update { it.copy(selectedTab = 1) }
        emitEvent(RecipePageEvent.ClickOnlineRecipes)
    }

    override suspend fun onSaveRecipeClick(recipe: Recipe) {
        recipeRepository.insertRecipe(recipe)
    }

    override suspend fun onDeleteRecipeClick(recipe: Recipe) {
        recipeRepository.deleteRecipe(recipe)
    }

    private fun emitEvent(event: RecipePageEvent) = viewModelScope.launch { _events.emit(event) }

    companion object

}