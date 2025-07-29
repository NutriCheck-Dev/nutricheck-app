package com.frontend.nutricheck.client.ui.view_model.search_food_product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID

sealed class SearchMode {
    data class IngredientsForRecipe(val recipeId: String) : SearchMode()
    data class ComponentsForMeal(val mealId: String) : SearchMode()
    object ForRecipePage : SearchMode()
    object LogNewMeal : SearchMode()
}

data class CommonSearchParameters(
    val language: String,
    val query: String,
    val selectedTab: Int,
    val results: List<FoodComponent>,
    val addedComponents: List<Pair<Double, FoodComponent>>
)

sealed class SearchUiState {
    abstract val parameters: CommonSearchParameters
    abstract fun updateParams(params: CommonSearchParameters): SearchUiState
    data class AddIngredientState(
        val recipeId: String,
        override val parameters: CommonSearchParameters,
    ): SearchUiState() {
        override fun updateParams(params: CommonSearchParameters): SearchUiState =
            copy(parameters = params)

    }

    data class AddComponentsToMealState(
        val mealId: String,
        val dayTime: DayTime? = null,
        override val parameters: CommonSearchParameters,
    ): SearchUiState() {
        override fun updateParams(params: CommonSearchParameters): SearchUiState =
            copy(parameters = params)
    }

    data class ForRecipePageState(
        override val parameters: CommonSearchParameters,
    ): SearchUiState() {
        override fun updateParams(params: CommonSearchParameters): SearchUiState =
            copy(parameters = params)
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
    private val historyRepository: HistoryRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : BaseFoodSearchOverviewViewModel() {
    private lateinit var _searchState: MutableStateFlow<SearchUiState>
    val searchState: StateFlow<SearchUiState> get() = _searchState.asStateFlow()

    private val mode: SearchMode =
        savedStateHandle.get<String>("recipeId")?.let { SearchMode.IngredientsForRecipe(it) }
            ?: savedStateHandle.get<String>("mealId")?.let { SearchMode.ComponentsForMeal(it) }
            ?: SearchMode.ForRecipePage

    init {
        viewModelScope.launch {
            val commonParams = CommonSearchParameters(
                language = appSettingsRepository.language.first().code,
                query = "",
                selectedTab = 0,
                results = emptyList(),
                addedComponents = emptyList()
            )
            val newMealId = UUID.randomUUID().toString()

            _searchState = MutableStateFlow(
                when (mode) {
                    is SearchMode.IngredientsForRecipe ->
                        SearchUiState.AddIngredientState(
                            recipeId = mode.recipeId,
                            parameters = commonParams
                        )
                    is SearchMode.ComponentsForMeal ->
                        SearchUiState.AddComponentsToMealState(
                            mealId = mode.mealId,
                            parameters = commonParams
                        )
                    is SearchMode.ForRecipePage ->
                        SearchUiState.ForRecipePageState(
                            parameters = commonParams
                        )
                    is SearchMode.LogNewMeal ->
                        SearchUiState.AddComponentsToMealState(
                            mealId = newMealId,
                            parameters = commonParams
                        )
                }
            )
        }
    }

    private val _events = MutableSharedFlow<SearchEvent>()
    val events: SharedFlow<SearchEvent> = _events.asSharedFlow()

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> { changeQuery(event.query) }
            is SearchEvent.DayTimeChanged -> { changeDayTime(event.dayTime) }
            is SearchEvent.AddFoodComponent -> onClickAddFoodComponent(event.foodComponent)
            is SearchEvent.RemoveFoodComponent -> onClickRemoveFoodComponent(event.foodComponent)
            is SearchEvent.Search -> onClickSearchFoodComponent()
            is SearchEvent.Retry -> onClickSearchFoodComponent()
            is SearchEvent.Clear -> cancelSearch()
            is SearchEvent.SubmitComponents -> addComponentsToMeal()
        }
    }

    override fun onClickSearchFoodComponent() {

    setLoading()

        viewModelScope.launch {

        }
    }

    override fun onClickAddFoodComponent(foodComponent: Pair<Double, FoodComponent>) =
        _searchState.update { state ->
            val currentParams = state.parameters
            val newParams = currentParams.copy(addedComponents = currentParams.addedComponents + foodComponent)
            state.updateParams(newParams)
        }

    override fun onClickRemoveFoodComponent(foodComponent: FoodComponent) =
        _searchState.update { state ->
            val currentParams = state.parameters
            val newParams = state.parameters.copy(addedComponents = currentParams.addedComponents.filterNot { it.second == foodComponent })
            state.updateParams(newParams)
        }

    private fun changeQuery(query: String) =
        _searchState.update { state ->
            val newParams = state.parameters.copy(query = query)
            state.updateParams(newParams)
        }


    private fun cancelSearch() =
        _searchState.update { state ->
            val newParams = state.parameters.copy(query = "", results = emptyList(),)
            state.updateParams(newParams)
        }


    private fun changeDayTime(dayTime: DayTime) =
        _searchState.update { state ->
            when (state) {
                is AddComponentsToMealState -> state.copy(dayTime = dayTime)
                else -> state
            }
        }

    private fun addComponentsToMeal() {
        val meal = MealEntity(
            id = UUID.randomUUID().toString(),
            historyDayDate = _searchState.value.date!!,
            dayTime = _searchState.value.dayTime!!
        )
        var mealFoodItemsWithProduct: List<Pair<Double, FoodProductEntity>>? = null
        var mealRecipeItemsWithRecipeEntity: List<Pair<Double, RecipeEntity>>? = null

        _searchState.value.addedComponents.forEach { component ->
            when (component.second) {
                is FoodProduct -> {
                    val foodProductEntity = DbFoodProductMapper.toFoodProductEntity(component.second as FoodProduct)
                    mealFoodItemsWithProduct = (mealFoodItemsWithProduct ?: emptyList()) + Pair(component.first, foodProductEntity)
                }
                is Recipe -> {
                    val recipeEntity = DbRecipeMapper.toRecipeEntity(component.second as Recipe)
                    mealRecipeItemsWithRecipeEntity = (mealRecipeItemsWithRecipeEntity ?: emptyList()) + Pair(
                        recipeEntity.servings,
                        recipeEntity
                    )
                }
            }
        }

        mealFoodItemsWithProduct = mealFoodItemsWithProduct?.takeIf { it.isNotEmpty() }
        mealRecipeItemsWithRecipeEntity = mealRecipeItemsWithRecipeEntity?.takeIf { it.isNotEmpty() }

        viewModelScope.launch {
            historyRepository.addMeal(
                meal = meal,
                mealFoodItemsWithProduct = mealFoodItemsWithProduct,
                mealRecipeItemsWithRecipeEntity = mealRecipeItemsWithRecipeEntity
            )
            _searchState.update { SearchState() }
        }
    }

}