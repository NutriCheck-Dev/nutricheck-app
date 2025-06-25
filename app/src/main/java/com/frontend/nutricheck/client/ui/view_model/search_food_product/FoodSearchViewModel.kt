package com.frontend.nutricheck.client.ui.view_model.search_food_product

import com.frontend.nutricheck.client.model.data_layer.FoodComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class SearchState(
    val query: String = "",
    val results: List<FoodComponent> = emptyList()
)

sealed interface  SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent
    data object Retry : SearchEvent
    data object Clear : SearchEvent
}

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    initialState: SearchState = SearchState()
) : BaseFoodSearchOverviewViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val searchJob = MutableStateFlow<Job?>(null)

    val _events = MutableSharedFlow<SearchEvent>()
    val events: SharedFlow<SearchEvent> = _events.asSharedFlow()

    fun onEvent(event: SearchEvent) {}

    override fun onClickSearchFoodProduct() {
        // Logic to handle search food product click
    }

    override fun onClickAddFoodProduct() {
        // Logic to handle add food product click
    }

    override fun onFoodClick() {
        TODO("Not yet implemented")
    }

    override fun onRecipeClick() {
        TODO("Not yet implemented")
    }

    override fun onMyRecipesClick() {
        TODO("Not yet implemented")
    }

    override fun onOnlieRecipesClick() {
        TODO("Not yet implemented")
    }

    private fun performSearch(query: String, force: Boolean = false) {}
}