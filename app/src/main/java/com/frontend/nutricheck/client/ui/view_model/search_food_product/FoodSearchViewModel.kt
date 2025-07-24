package com.frontend.nutricheck.client.ui.view_model.search_food_product

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
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
import java.io.IOException

data class SearchState(
    val query: String = "",
    val selectedTab: Int = 0,
    val results: List<FoodComponent> = emptyList(),
    val addedComponents: List<FoodComponent> = emptyList(),
    val isFromAddIngredient: Boolean = false
)

sealed interface  SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent
    data class AddFoodComponent(val foodComponent: FoodComponent) : SearchEvent
    data class RemoveFoodComponent(val foodComponent: FoodComponent) : SearchEvent
    object Search : SearchEvent
    object Retry : SearchEvent
    object Clear : SearchEvent
}

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val recipeRepository: RecipeRepositoryImpl,
    private val foodProductRepository: FoodProductRepositoryImpl
) : BaseFoodSearchOverviewViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _events = MutableSharedFlow<SearchEvent>()
    val events: SharedFlow<SearchEvent> = _events.asSharedFlow()

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _searchState.update { it.copy(query = event.query) }
            }
            is SearchEvent.AddFoodComponent -> onClickAddFoodComponent(event.foodComponent)
            is SearchEvent.RemoveFoodComponent -> onClickRemoveFoodComponent(event.foodComponent)
            is SearchEvent.Search -> onClickSearchFoodComponent()
            is SearchEvent.Retry -> onClickSearchFoodComponent()
            is SearchEvent.Clear -> _searchState.update { it.copy(results = emptyList(), query = "") }
        }
    }

    override fun onClickSearchFoodComponent() {
        setLoading()

        viewModelScope.launch {
            val query = _searchState.value.query.trim()
            if (query.isEmpty()) {
                _searchState.value = SearchState()
                setReady()
                return@launch
            }

            try {
                val foodProducts = foodProductRepository.searchFoodProduct(query)
                val recipes = (recipeRepository.searchRecipe(query)
                        as? Result.Success)?.data.orEmpty()
                val mixed = (foodProducts + recipes)
                    .sortedBy { it.name }

                _searchState.update { it.copy(results = mixed) }
                setReady()
            } catch (io: IOException) {
                setError("Netzwerkfehler: Bitte überprüfen Sie Ihre Internetverbindung.")
            } catch (e: Exception) {
                setError("Ein unerwarteter Fehler ist aufgetreten: ${e.message}")
            }
        }
    }

    override fun onClickAddFoodComponent(foodComponent: FoodComponent) =
        _searchState.update { state ->
            state.copy(addedComponents = state.addedComponents + foodComponent)
        }

    override fun onClickRemoveFoodComponent(foodComponent: FoodComponent) =
        _searchState.update { state ->
            state.copy(addedComponents = state.addedComponents - foodComponent)
        }

    override fun onFoodClick() {
        TODO("Not yet implemented")
    }

    override fun onRecipeClick() {
        TODO("Not yet implemented")
    }
}