package com.frontend.nutricheck.client.ui.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
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
import dagger.hilt.android.qualifiers.ApplicationContext
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
    object LogNewMeal : SearchMode()
}

data class CommonSearchParameters(
    val language: String = "",
    val query: String = "",
    val selectedTab: Int = 0,
    val generalResults: List<FoodComponent> = emptyList(),
    val localRecipesResults: List<Recipe> = emptyList(),
    val addedComponents: List<FoodComponent> = emptyList(),
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
    }
    data class AddComponentsToMealState(
        val mealId: String,
        val dayTime: DayTime? = null,
        val date: Long? = null,
        override val parameters: CommonSearchParameters,
    ) : SearchUiState() {
        override fun updateParams(params: CommonSearchParameters): SearchUiState =
            copy(parameters = params)
    }
}

sealed interface  SearchEvent {
    data class DayTimeChanged(val dayTime: DayTime) : SearchEvent
    data class QueryChanged(val query: String) : SearchEvent
    data class AddFoodComponent(val foodComponent: FoodComponent) : SearchEvent
    data class RemoveFoodComponent(val foodComponent: FoodComponent) : SearchEvent
    object Search : SearchEvent
    object Retry : SearchEvent
    object Clear : SearchEvent
    object ClickSearchAll : SearchEvent
    object ClickSearchMyRecipes : SearchEvent
    object SubmitComponentsToMeal : SearchEvent
    object MealSelectorClick: SearchEvent
    object ExpandBottomSheet : SearchEvent
    object ResetErrorState : SearchEvent
    data object MealSaved : SearchEvent
}

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val appSettingRepository : AppSettingRepository,
    private val recipeRepository: RecipeRepository,
    private val foodProductRepository: FoodProductRepository,
    private val historyRepository: HistoryRepository,
    private val combinedSearchListStore: CombinedSearchListStore,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

        private val mode: SearchMode =
            savedStateHandle
                    .get<String>("mealId")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { SearchMode.ComponentsForMeal(it) }
                ?: SearchMode.LogNewMeal

    private val newMealId = UUID.randomUUID().toString()
    private val initialCommonParams = CommonSearchParameters()

    private val initialState: SearchUiState =
        when (mode) {
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
            savedStateHandle.get<String>("date")
                ?.let { date ->
                    _searchState.update { state ->
                        Log.v("XD", "SearchViewModel init with mode: $date")
                        when (state) {
                            is SearchUiState.AddComponentsToMealState ->
                                state.copy(date = date.toLong())
                            else -> state
                        }
                    }
                }
            savedStateHandle
                .getStateFlow<FoodComponent?>("newComponent", null)
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
            is SearchEvent.ClickSearchAll -> onSearchAllClick()
            is SearchEvent.ClickSearchMyRecipes -> onSearchMyRecipesClick()
            is SearchEvent.ResetErrorState -> setReady()
            is SearchEvent.MealSaved -> null
        }
    }

    private fun onClickSearchFoodComponent() {
        val query = _searchState.value.parameters.query
        if (query.isBlank()) {
            return
        }
        viewModelScope.launch {
            val language = _searchState.value.parameters.language
            when (mode) {
                is SearchMode.ComponentsForMeal,
                     SearchMode.LogNewMeal -> {
                         if (_searchState.value.parameters.selectedTab == 1) {
                             recipeRepository.getRecipesByName(query).let { recipes ->
                                 if (recipes.isEmpty()) {
                                     return@launch
                                 } else {
                                     _searchState.update { state ->
                                         state.updateParams(state.parameters.copy(localRecipesResults = recipes))
                                     }
                                     val recipeList = _searchState.value.parameters.localRecipesResults
                                     combinedSearchListStore.update(recipeList)
                                     setReady()
                                     return@launch
                                 }
                             }
                         } else {
                    val foodProductFlow = foodProductRepository
                        .searchFoodProducts(query, language)

                    val recipeFlow = recipeRepository
                        .searchRecipes(query)

                    val merged: Flow<Result<List<FoodComponent>>> =
                        merge(foodProductFlow, recipeFlow)
                            .onStart { setLoading() }
                            .scan(emptyList<FoodComponent>()) { acc, search ->
                                when (search) {
                                    is Result.Success -> {
                                        val newAcc = acc + search.data
                                        newAcc
                                    }

                                    is Result.Error -> {
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
                                                generalResults = result.data
                                            )
                                        )
                                    }
                                    setReady()
                                }

                                is Result.Error -> {
                                    setError(result.message!!)
                                }
                            }
                            val combinedList = _searchState.value.parameters.generalResults +
                                    _searchState.value.parameters.addedComponents
                            combinedSearchListStore.update(combinedList)
                        } }
                }
            }
        }
    }

    private fun onClickAddFoodComponent(foodComponent: FoodComponent) {
        _searchState.update { state ->
            val currentParams = state.parameters
            val existing = currentParams.addedComponents.find { it.id == foodComponent.id }
            val newAddedComponents = if (existing != null) {
                currentParams.addedComponents
                    .filterNot { it.id == foodComponent.id } +
                        when(foodComponent) {
                            is FoodProduct ->
                                FoodProduct(
                                    id = foodComponent.id,
                                    name = foodComponent.name,
                                    calories = foodComponent.calories,
                                    carbohydrates = foodComponent.carbohydrates,
                                    protein = foodComponent.protein,
                                    fat = foodComponent.fat,
                                    servings = foodComponent.servings,
                                    servingSize = foodComponent.servingSize,
                            )
                            is Recipe ->
                                Recipe(
                                    id = foodComponent.id,
                                    name = foodComponent.name,
                                    calories = foodComponent.calories,
                                    carbohydrates = foodComponent.carbohydrates,
                                    protein = foodComponent.protein,
                                    fat = foodComponent.fat,
                                    ingredients = foodComponent.ingredients.map {
                                        Ingredient(
                                            it.recipeId,
                                            it.foodProduct
                                        )
                                    },
                                    servings = foodComponent.servings
                                )
                        }

            } else {
                currentParams.addedComponents + foodComponent
            }
            val newParams =
                currentParams.copy(
                    addedComponents = newAddedComponents,
                    generalResults = currentParams.generalResults.filterNot { it.id == foodComponent.id },
                )
            val combinedList = newParams.generalResults + newParams.addedComponents
            combinedSearchListStore.update(combinedList)
            state.updateParams(newParams)
        }
        viewModelScope.launch {
            _events.emit(SearchEvent.AddFoodComponent(foodComponent))
        }
    }

    private fun onClickRemoveFoodComponent(foodComponent: FoodComponent) =
        _searchState.update { state ->
            val currentParams = state.parameters
            val newParams =
                state.parameters.copy(
                    generalResults = currentParams.generalResults + foodComponent,
                    addedComponents = currentParams.addedComponents.filterNot { it.id == foodComponent.id }
                )
            val combinedList = newParams.generalResults + newParams.addedComponents
            combinedSearchListStore.update(combinedList)
            state.updateParams(newParams)
        }

    private fun changeQuery(query: String) =
        _searchState.update { state ->
            val newParams = state.parameters.copy(query = query)
            state.updateParams(newParams)
        }


    private fun cancelSearch() =
        _searchState.update { state ->
            val newParams = state.parameters.copy(
                query = "",
                generalResults = emptyList(),
                localRecipesResults = emptyList())
            combinedSearchListStore.update(emptyList())
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

        if (state.dayTime == null) {
            setError(appContext.getString(R.string.error_missing_dayTime))
            return
        }

        val (foodProducts, recipes) = state.parameters.addedComponents
            .partition { it is FoodProduct }

        val mealFoodItems = foodProducts.map { component ->
            MealFoodItem(
                mealId = state.mealId,
                foodProduct = component as FoodProduct
            )
        }

        val mealRecipeItems = recipes.map { component ->
            MealRecipeItem(
                mealId = state.mealId,
                recipe = component as Recipe
            )
        }

        if (mealFoodItems.isEmpty() && mealRecipeItems.isEmpty()) {
            setError(appContext.getString(R.string.error_create_meal_missing_foodComponent))
            return
        }

        viewModelScope.launch {
            val mealDate = state.date ?: Date().time
            setLoading()
            try {
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
                        calories = (mealFoodItems).sumOf { it.foodProduct.calories * it.quantity }
                                + (mealRecipeItems).sumOf { it.recipe.calories * it.quantity },
                        carbohydrates = (mealFoodItems).sumOf { it.foodProduct.carbohydrates * it.quantity }
                                + (mealRecipeItems).sumOf { it.recipe.carbohydrates * it.quantity },
                        protein = (mealFoodItems).sumOf { it.foodProduct.protein * it.quantity }
                                + (mealRecipeItems).sumOf { it.recipe.protein * it.quantity },
                        fat = (mealFoodItems).sumOf { it.foodProduct.fat * it.quantity }
                                + (mealRecipeItems).sumOf { it.recipe.fat * it.quantity },
                        date = Date(mealDate),
                        dayTime = state.dayTime,
                        mealFoodItems = mealFoodItems,
                        mealRecipeItems = mealRecipeItems

                    )
                    historyRepository.addMeal(newMeal)
                    _events.emit(SearchEvent.MealSaved)
                }
                setReady()
            } catch (e: Exception) {
                setError("Failed to save meal: ${e.message}")
            }
        }
    }

    private fun onSearchAllClick() =
        _searchState.update { state ->
            state.updateParams(state.parameters.copy(selectedTab = 0))
        }

    private fun onSearchMyRecipesClick() =
        _searchState.update { state ->
            state.updateParams(state.parameters.copy(selectedTab = 1))
        }
}
