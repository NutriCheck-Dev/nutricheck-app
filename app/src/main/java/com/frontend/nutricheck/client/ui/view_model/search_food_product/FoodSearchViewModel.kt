package com.frontend.nutricheck.client.ui.view_model.search_food_product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed class SearchMode {
    data class ComponentsForMeal(val mealId: String) : SearchMode()
    data class IngredientsForRecipe(val recipeId: String) : SearchMode()
    object LogNewMeal : SearchMode()
}

data class CommonSearchParameters(
    val language: String = "",
    val query: String = "",
    val selectedTab: Int = 0,
    val results: List<FoodComponent> = emptyList(),
    val addedComponents: List<Pair<Double, FoodComponent>> = emptyList()
)

sealed class SearchUiState {
    abstract val parameters: CommonSearchParameters
    abstract fun updateParams(params: CommonSearchParameters): SearchUiState
    data class AddIngredientState(
        val recipeId: String,
        override val parameters: CommonSearchParameters,
    ) : SearchUiState() {
        override fun updateParams(params: CommonSearchParameters): SearchUiState =
            copy(parameters = params)

    }
    data class AddComponentsToMealState(
        val mealId: String,
        val dayTime: DayTime? = null,
        override val parameters: CommonSearchParameters,
    ) : SearchUiState() {
        override fun updateParams(params: CommonSearchParameters): SearchUiState =
            copy(parameters = params)
    }
}

sealed interface  SearchEvent {
    data class DayTimeChanged(val dayTime: DayTime) : SearchEvent
    data class QueryChanged(val query: String) : SearchEvent
    data class AddFoodComponent(val foodComponent: Pair<Double, FoodComponent>) : SearchEvent
    data class RemoveFoodComponent(val foodComponent: FoodComponent) : SearchEvent
    object Search : SearchEvent
    object Retry : SearchEvent
    object Clear : SearchEvent
    object SubmitComponents : SearchEvent
}

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val appSettingsRepository : AppSettingsRepository,
    private val recipeRepository: RecipeRepositoryImpl,
    private val foodProductRepository: FoodProductRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : BaseFoodSearchOverviewViewModel() {
    private val mode: SearchMode =
        savedStateHandle.get<String>("recipeId")?.let { SearchMode.IngredientsForRecipe(it) }
            ?: savedStateHandle.get<String>("mealId")?.let { SearchMode.ComponentsForMeal(it) }
            ?: SearchMode.LogNewMeal

    private val newMealId = UUID.randomUUID().toString()
    private val initialCommonParams = CommonSearchParameters()

    private val initialState: SearchUiState =
        when (mode) {
            is SearchMode.IngredientsForRecipe ->
                SearchUiState.AddIngredientState(
                    recipeId = mode.recipeId,
                    parameters = initialCommonParams
                )

            is SearchMode.ComponentsForMeal ->
                SearchUiState.AddComponentsToMealState(
                    mealId = mode.mealId,
                    parameters = initialCommonParams
                )

            is SearchMode.LogNewMeal ->
                SearchUiState.AddComponentsToMealState(
                    mealId = newMealId,
                    parameters = initialCommonParams
                )
        }

    private var _searchState = MutableStateFlow(initialState)
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    init {
        viewModelScope.launch {
            appSettingsRepository.language.collect { language ->
                _searchState.update { uiState ->
                    uiState.updateParams(uiState.parameters.copy(language = language.code))
                }
            }
        }
    }

    private val _events = MutableSharedFlow<SearchEvent>()
    val events: SharedFlow<SearchEvent> = _events.asSharedFlow()

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                changeQuery(event.query)
            }

            is SearchEvent.DayTimeChanged -> {
                changeDayTime(event.dayTime)
            }
            is SearchEvent.AddFoodComponent -> onClickAddFoodComponent(event.foodComponent)
            is SearchEvent.RemoveFoodComponent -> onClickRemoveFoodComponent(event.foodComponent)
            is SearchEvent.Search -> onClickSearchFoodComponent()
            is SearchEvent.Retry -> onClickSearchFoodComponent()
            is SearchEvent.Clear -> cancelSearch()
            is SearchEvent.SubmitComponents -> addComponentsToMeal()
        }
    }

    override fun onClickSearchFoodComponent() {
        val query = _searchState.value.parameters.query
        if (query.isBlank()) {
            setError("Please enter a search term.")
            return
        }
        viewModelScope.launch {
            val language = _searchState.value.parameters.language
            val flow: Flow<Result<List<FoodComponent>>> = when (mode) {
                is SearchMode.IngredientsForRecipe -> {
                    foodProductRepository
                        .searchFoodProducts(query, language)
                        .map { result -> result.mapData { list -> list.map { it as FoodProduct }}}
                }

                else -> {
                    combine(
                        foodProductRepository.searchFoodProducts(query, language),
                        recipeRepository.searchRecipes(query)
                    ) { foodProductResults, recipeResults ->
                        when {
                            foodProductResults is Result.Error -> foodProductResults.toError<List<FoodComponent>>()
                            recipeResults is Result.Error -> recipeResults.toError<List<FoodComponent>>()
                            foodProductResults is Result.Success && recipeResults is Result.Success -> {
                                val mixedResults = (foodProductResults.data + recipeResults.data)
                                    .sortedBy { it.name }
                                Result.Success(mixedResults)
                            }
                            else -> Result.Success(emptyList())
                        }
                    }
                }
            }
            flow
                .onStart { setLoading() }
                .catch { setError(it.message!!) }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _searchState.update { state ->
                                state.updateParams(
                                    state.parameters.copy(
                                        query = query,
                                        results = result.data
                                    )
                                )
                            }
                        }
                        is Result.Error -> {
                            setError(result.message!!)
                        }
                    }
                }


        }
    }

    override fun onClickAddFoodComponent(foodComponent: Pair<Double, FoodComponent>) =
        _searchState.update { state ->
            val currentParams = state.parameters
            val newParams =
                currentParams.copy(addedComponents = currentParams.addedComponents + foodComponent)
            state.updateParams(newParams)
        }

    override fun onClickRemoveFoodComponent(foodComponent: FoodComponent) =
        _searchState.update { state ->
            val currentParams = state.parameters
            val newParams =
                state.parameters.copy(addedComponents = currentParams.addedComponents.filterNot { it.second == foodComponent })
            state.updateParams(newParams)
        }

    private fun changeQuery(query: String) =
        _searchState.update { state ->
            val newParams = state.parameters.copy(query = query)
            state.updateParams(newParams)
        }


    private fun cancelSearch() =
        _searchState.update { state ->
            val newParams = state.parameters.copy(query = "", results = emptyList())
            state.updateParams(newParams)
        }


    private fun changeDayTime(dayTime: DayTime) =
        _searchState.update { state ->
            when (state) {
                is SearchUiState.AddComponentsToMealState -> state.copy(dayTime = dayTime)
                else -> state
            }
        }

    private fun addComponentsToMeal() {

    }

    private fun submitComponentsToRecipe() : List<Ingredient> {
        val state = _searchState.value
        return when (state) {
            is SearchUiState.AddIngredientState -> state.parameters.addedComponents.map {
                Ingredient(state.recipeId, it.second as FoodProduct, quantity = it.first) }
            else -> emptyList()
        }
    }

    private inline fun <T, R> Result<T>.mapData(transform: (T)->R): Result<R> =
        when (this) {
            is Result.Success -> Result.Success(transform(data))
            is Result.Error -> Result.Error(code, message)
        }

    @Suppress("UNCHECKED_CAST")
    fun <T> Result<*>.toError(): Result<T> =
        Result.Error(
            code = (this as Result.Error).code,
            message = this.message
        )
}
