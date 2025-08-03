package com.frontend.nutricheck.client.ui.view_model.recipe.page

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipePageState(
    val myRecipes: List<Recipe> = emptyList(),
    val onlineRecipes: List<Recipe> = emptyList(),
    val selectedTab: Int = 0,
    val query: String = ""
)

sealed interface RecipePageEvent {
    data object ClickMyRecipes : RecipePageEvent
    data object ClickOnlineRecipes : RecipePageEvent
    data class ClickSaveRecipe(val recipe: Recipe) : RecipePageEvent
    data class ClickDeleteRecipe(val recipe: Recipe) : RecipePageEvent
    data class QueryChanged(val query: String) : RecipePageEvent
    object SearchOnline : RecipePageEvent
}

@HiltViewModel
class RecipePageViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val recipeRepository: RecipeRepository
) : BaseRecipePageViewModel() {

    private val _recipePageState = MutableStateFlow(RecipePageState())
    val recipePageState: StateFlow<RecipePageState> = _recipePageState.asStateFlow()

    init {
        viewModelScope.launch {
            setLoading()
            val myRecipes = recipeRepository.getMyRecipes()
            val onlineRecipes = emptyList<Recipe>()
            _recipePageState.update { it.copy(
                myRecipes = myRecipes,
                onlineRecipes = onlineRecipes,
                selectedTab = 0,
                query = ""
            ) }
            setReady()
        }
    }

    private val _events = MutableSharedFlow<RecipePageEvent>()
    val events: SharedFlow<RecipePageEvent> = _events.asSharedFlow()

    fun onEvent(event: RecipePageEvent) {
        when(event) {
            is RecipePageEvent.ClickMyRecipes -> onMyRecipesClick()
            is RecipePageEvent.ClickOnlineRecipes -> onOnlineRecipesClick()
            is RecipePageEvent.ClickSaveRecipe -> viewModelScope.launch { onSaveRecipeClick(event.recipe) }
            is RecipePageEvent.ClickDeleteRecipe -> viewModelScope.launch { onDeleteRecipeClick(event.recipe) }
            is RecipePageEvent.QueryChanged -> _recipePageState.update { it.copy(query = event.query) }
            RecipePageEvent.SearchOnline -> viewModelScope.launch { performOnlineSearch() }
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

    private fun performOnlineSearch() {
        val query = _recipePageState.value.query.trim()
        if (query.isBlank()) {
            _recipePageState.update { it.copy(onlineRecipes = emptyList()) }
            setReady()
            return
        }
        viewModelScope.launch {
            recipeRepository
                .searchRecipes(query)
                .onStart {
                    setLoading()
                    _recipePageState.update { it.copy(onlineRecipes = emptyList()) }
                }
                .catch { it ->
                    _recipePageState.update { it.copy(onlineRecipes = emptyList()) }
                    setError(it.message!!)
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _recipePageState.update { it.copy(onlineRecipes = result.data) }
                            setReady()
                        }
                        is Result.Error -> {
                            _recipePageState.update { it.copy(onlineRecipes = emptyList()) }
                            setError(result.message!!)
                        }
                    }
                }
        }
    }
}