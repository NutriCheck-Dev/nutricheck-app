package com.frontend.nutricheck.client.ui.view_model.recipe.page

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

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
    private val recipeRepository: RecipeRepositoryImpl
) : BaseRecipePageViewModel() {


    private val _recipePageState = MutableStateFlow(RecipePageState())
    val recipePageState: StateFlow<RecipePageState> = _recipePageState.asStateFlow()
    private val _onlineResults = MutableStateFlow<List<Recipe>>(emptyList())

    init {
        viewModelScope.launch {
            combine(
                recipeRepository.getMyRecipesWithIngredients(),
                _onlineResults,
                _recipePageState.map { it.selectedTab },
                _recipePageState.map { it.query }
            ) { myRecipes, onlineRecipes, tab, query ->
                val filteredMyRecipes = myRecipes.map { it.recipe }.sortedBy { it.name }
                val filteredOnlineRecipes = if (tab == 1 && query.isBlank()) {
                    onlineRecipes
                } else {
                    emptyList()
                }
                RecipePageState(
                    myRecipes = filteredMyRecipes,
                    onlineRecipes = filteredOnlineRecipes,
                    selectedTab = tab,
                    query = query
                )
            }.collect { newState ->
                _recipePageState.update { newState }
            }
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
            //is RecipePageEvent.ShowSnackbar -> emitEvent(RecipePageEvent.ShowSnackbar(event.message))
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
        viewModelScope.launch {
            setLoading()

            val query = _recipePageState.value.query.trim()
            if (query.isBlank()) {
                _onlineResults.value = emptyList()
                setReady()
                return@launch
            }
            try {
                when (val result = recipeRepository.searchRecipe(query)) {
                    is Result.Success -> {
                        _onlineResults.value = result.data
                        setReady()}
                    is Result.Error -> {
                        _onlineResults.value = emptyList()
                        setError("Server-Fehler beim Abrufen der Rezepte: ${result.message}")
                    }
                }
            } catch (io: IOException) {
                _onlineResults.value = emptyList()
                setError("Netzwerkfehler: ${io.message ?: "Unbekannter Fehler"}")
            } catch (e: Exception) {
                _onlineResults.value = emptyList()
                setError("Unerwarteter Fehler: ${e.message ?: ""}")
            }
        }
    }
}