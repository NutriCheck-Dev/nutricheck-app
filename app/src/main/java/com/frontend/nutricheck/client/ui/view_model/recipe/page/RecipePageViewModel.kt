package com.frontend.nutricheck.client.ui.view_model.recipe.page

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipePageState(
    val recipes: List<Recipe> = emptyList(),
    val myRecipes: List<Recipe> = emptyList(),
    val onlineRecipes: List<Recipe> = emptyList()
)

sealed interface RecipePageEvent {
    data object ClickAddRecipe : RecipePageEvent
    data object ClickRecipe : RecipePageEvent
    data object ClickDetails : RecipePageEvent
    data object ClickMyRecipes : RecipePageEvent
    data object ClickOnlineRecipes : RecipePageEvent
}

@HiltViewModel
class RecipePageViewModel @Inject constructor(
    initialState: RecipePageState = RecipePageState(),
    val recipeRepository: RecipeRepository
) : BaseRecipePageViewModel() {

    private val _recipePageState = MutableStateFlow(RecipePageState())
    val createRecipeState = _recipePageState.asStateFlow()

    private val _events = MutableSharedFlow<RecipePageEvent>()
    val events: SharedFlow<RecipePageEvent> = _events.asSharedFlow()

    fun onEvent(event: RecipePageEvent) {}

    override fun onClickAddRecipe() { emitEvent(RecipePageEvent.ClickAddRecipe) }
    override fun onRecipeClick() { emitEvent(RecipePageEvent.ClickRecipe) }
    override fun onDetailsClick() { emitEvent(RecipePageEvent.ClickDetails) }
    override fun onMyRecipesClick() { emitEvent(RecipePageEvent.ClickMyRecipes) }
    override fun onOnlieRecipesClick() { emitEvent(RecipePageEvent.ClickOnlineRecipes) }
    override fun displayPublicRecipes() {
        // Implementation to display public recipes
    }
    override fun displayOwnerRecipes() {
        // Implementation to display recipes owned by the user
    }

    private fun emitEvent(event: RecipePageEvent) = viewModelScope.launch { _events.emit(event) }

    companion object

}