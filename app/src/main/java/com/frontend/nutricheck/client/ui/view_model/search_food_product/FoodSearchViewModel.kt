package com.frontend.nutricheck.client.ui.view_model.search_food_product

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import kotlin.collections.map

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
    val addedComponents: List<Pair<Double, FoodComponent>> = emptyList(),
    val expanded: Boolean = false,
    val bottomSheetExpanded: Boolean = false
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

        fun submitComponentsToRecipe() : List<Ingredient> {
            val ingredients: List<Ingredient> = parameters.addedComponents.map {
                        Ingredient(recipeId, it.second as FoodProduct, quantity = it.first)
                    }
            return ingredients
        }
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
    object SubmitComponentsToMeal : SearchEvent
    object MealSelectorClick: SearchEvent
    object ExpandBottomSheet : SearchEvent
}

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val appSettingRepository : AppSettingRepository,
    private val recipeRepository: RecipeRepository,
    private val foodProductRepository: FoodProductRepository,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : BaseFoodSearchOverviewViewModel() {

        private val mode: SearchMode =
            savedStateHandle
                .get<String>("recipeId")
                ?.takeIf { it.isNotBlank() }
                ?.let { SearchMode.IngredientsForRecipe(it) }
                ?: savedStateHandle
                    .get<String>("mealId")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { SearchMode.ComponentsForMeal(it) }
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
            savedStateHandle.get<String>("dayTime")
                ?.let { runCatching { DayTime.valueOf(it) }.getOrNull() }
                ?.let { dayTime ->
                    _searchState.update { state ->
                        when (state) {
                            is SearchUiState.AddComponentsToMealState ->
                                state.copy(dayTime = dayTime)
                            else -> state
                        }
                    }
                }
            val date = savedStateHandle.get<String>("date")?.toLongOrNull()?.let { Date(it) }
            savedStateHandle
                .getStateFlow<Pair<Double, FoodComponent>?>("newComponent", null)
                .filterNotNull()
                .collect { component ->
                    onEvent(SearchEvent.AddFoodComponent(component))
                    savedStateHandle.remove<Pair<Double, FoodComponent>>("newComponent")
                }
            appSettingRepository.language.collect { language ->
                _searchState.update { uiState ->
                    uiState.updateParams(uiState.parameters.copy(language = language.code))
                }
            }
        }
    }

    private val _events = MutableSharedFlow<SearchEvent>()
    val events: SharedFlow<SearchEvent> = _events.asSharedFlow()

    /**private val _addComponent = MutableSharedFlow<Pair<Double, FoodComponent>>()
    val addComponent: SharedFlow<Pair<Double, FoodComponent>> = _addComponent.asSharedFlow()**/

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
            is SearchEvent.SubmitComponentsToMeal -> submitComponentsToMeal()
            is SearchEvent.MealSelectorClick -> onClickMealSelector()
            is SearchEvent.ExpandBottomSheet -> {
                _searchState.update { state ->
                    val newParams = state.parameters.copy(bottomSheetExpanded = !state.parameters.bottomSheetExpanded)
                    state.updateParams(newParams)
                }
            }
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
            when (mode) {
                is SearchMode.IngredientsForRecipe -> {
                    val foodProducts = foodProductRepository
                        .searchFoodProducts(query, language)
                    foodProducts
                        .onStart { setLoading() }
                        .catch { setError(it.message!!) }
                        .collect { result ->
                            when (result) {
                                is Result.Success -> {
                                    _searchState.update { state ->
                                        state.updateParams(
                                            state.parameters.copy(
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
                    setReady()
                }

                else -> {
                    val foodProductFlow = foodProductRepository
                        .searchFoodProducts(query, language)
                        .map { resultProducts -> resultProducts.mapData { foodProducts -> foodProducts.map { it } } }

                    val recipeFlow = recipeRepository
                        .searchRecipes(query)
                        .map { resultRecipe -> resultRecipe.mapData { recipes -> recipes.map { it } } }

                    val merged: Flow<Result<List<FoodComponent>>> =
                        merge(foodProductFlow, recipeFlow)
                            .onStart { setLoading() }
                            .scan(emptyList<FoodComponent>()) { acc, search ->
                                Log.d("FoodSearchVM", "raw search emission → $search")
                                when (search) {
                                    is Result.Success -> {
                                        val newAcc = acc + search.data
                                        Log.d("FoodSearchVM", "raw search emission → $newAcc")
                                        newAcc
                                    }

                                    is Result.Error -> {
                                        Log.d("FoodSearchVM", "raw search emission → $acc")
                                        acc
                                    }
                                }
                            }
                            .map { Result.Success(it) }
                    merged
                        .onStart { setLoading() }
                        .catch { setError(it.message!!) }
                        .collect { result ->
                            when (result) {
                                is Result.Success -> {
                                    _searchState.update { state ->
                                        state.updateParams(
                                            state.parameters.copy(
                                                results = result.data
                                            )
                                        )
                                    }
                                    setReady()
                                }

                                is Result.Error -> {
                                    setError(result.message!!)
                                }
                            }
                        }
                }
            }
        }
    }

    override fun onClickAddFoodComponent(foodComponent: Pair<Double, FoodComponent>) {
        _searchState.update { state ->
            val currentParams = state.parameters
            val existing = currentParams.addedComponents.find { it.second.id == foodComponent.second.id }
            val newAddedComponents = if (existing != null) {
                currentParams.addedComponents
                    .filterNot { it.second.id == foodComponent.second.id } +
                        (existing.first + foodComponent.first to foodComponent.second)
            } else {
                currentParams.addedComponents + foodComponent
            }
            val newParams =
                currentParams.copy(addedComponents = newAddedComponents)
            state.updateParams(newParams)
        }
    }

    override fun onClickRemoveFoodComponent(foodComponent: FoodComponent) =
        _searchState.update { state ->
            val currentParams = state.parameters
            val newParams =
                state.parameters.copy(addedComponents = currentParams.addedComponents.filterNot { it.second.id == foodComponent.id })
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

    private fun onClickMealSelector() {
        _searchState.update { state ->
            val newParams = state.parameters.copy(expanded = !state.parameters.expanded)
            state.updateParams(newParams)
        }
    }


    private fun changeDayTime(dayTime: DayTime) =
        _searchState.update { state ->
            when (state) {
                is SearchUiState.AddComponentsToMealState -> state.copy(dayTime = dayTime)
                else -> state
            }
        }

    private fun submitComponentsToMeal() {
        val state = _searchState.value
        if (state !is SearchUiState.AddComponentsToMealState) return

        val (foodPairs, recipePairs) = state.parameters.addedComponents
            .partition { it.second is FoodProduct }

        val mealFoodItems = foodPairs.map { (quantity, component) ->
            MealFoodItem(
                mealId = state.mealId,
                foodProduct = component as FoodProduct,
                quantity = quantity
            )
        }

        val mealRecipeItems = recipePairs.map { (quantity, component) ->
            MealRecipeItem(
                mealId = state.mealId,
                recipe = component as Recipe,
                quantity = quantity
            )
        }
        viewModelScope.launch {
            if (mode is SearchMode.ComponentsForMeal) {
                val originalMeal = historyRepository.getMealById(state.mealId)
                originalMeal.copy(
                    mealFoodItems = originalMeal.mealFoodItems + mealFoodItems,
                    mealRecipeItems = originalMeal.mealRecipeItems + mealRecipeItems
                )
                historyRepository.updateMeal(originalMeal)
            } else {
                val newMeal = Meal(
                    id = state.mealId,
                    calories = (mealFoodItems).sumOf { it.foodProduct.calories * it.quantity}
                    + (mealRecipeItems).sumOf { it.recipe.calories * it.quantity },
                    carbohydrates = (mealFoodItems).sumOf { it.foodProduct.carbohydrates * it.quantity }
                    + (mealRecipeItems).sumOf { it.recipe.carbohydrates * it.quantity },
                    protein = (mealFoodItems).sumOf { it.foodProduct.protein * it.quantity }
                    + (mealRecipeItems).sumOf { it.recipe.protein * it.quantity },
                    fat = (mealFoodItems).sumOf { it.foodProduct.fat * it.quantity }
                    + (mealRecipeItems).sumOf { it.recipe.fat * it.quantity },
                    date = Date(),
                    dayTime = state.dayTime!!,
                    mealFoodItems = mealFoodItems,
                    mealRecipeItems = mealRecipeItems

                )
                historyRepository.addMeal(newMeal)
            }
        }
    }

    private fun submitComponentsToRecipe() : List<Ingredient> {
        val state = _searchState.value
        val ingredients: List<Ingredient> = when (mode) {
            is SearchMode.IngredientsForRecipe -> {
                state.parameters.addedComponents.map {
                    Ingredient(mode.recipeId, it.second as FoodProduct, quantity = it.first)
                }
            }
            else -> emptyList()
        }
        return ingredients
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
