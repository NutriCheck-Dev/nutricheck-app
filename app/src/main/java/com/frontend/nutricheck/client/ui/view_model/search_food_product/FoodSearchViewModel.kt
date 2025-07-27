package com.frontend.nutricheck.client.ui.view_model.search_food_product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepositoryImpl
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
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID

data class SearchState(
    val language: String = "de",
    val date: Date? = null,
    val dayTime: DayTime? = null,
    val query: String = "",
    val selectedTab: Int = 0,
    val results: List<FoodComponent> = emptyList(),
    val addedComponents: List<FoodComponent> = emptyList(),
    val fromAddIngredient: Boolean = false
)

sealed interface  SearchEvent {
    data class DayTimeChanged(val dayTime: DayTime) : SearchEvent
    data class QueryChanged(val query: String) : SearchEvent
    data class AddFoodComponent(val foodComponent: FoodComponent) : SearchEvent
    data class RemoveFoodComponent(val foodComponent: FoodComponent) : SearchEvent
    object Search : SearchEvent
    object Retry : SearchEvent
    object Clear : SearchEvent
    object SubmitComponentsToMeal : SearchEvent
}

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val userDataRepository: UserDataRepositoryImpl,
    private val recipeRepository: RecipeRepositoryImpl,
    private val foodProductRepository: FoodProductRepositoryImpl,
    private val historyRepository: HistoryRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : BaseFoodSearchOverviewViewModel() {

    private val fromAddIngredient = savedStateHandle.get<Boolean>("fromAddIngredient")
    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    init {
        viewModelScope.launch {
            val userData = userDataRepository.getUserData()
            _searchState.update { it.copy(language = userData.language) }
        }
        fromAddIngredient?.let {
            _searchState.update { it.copy(fromAddIngredient = fromAddIngredient) }
        }
        if (_searchState.value.date == null) {
            val today = LocalDate.now()
            val startOfDay = today.atStartOfDay(ZoneId.systemDefault())
            _searchState.update { it.copy(date = Date.from(startOfDay.toInstant())) }
        }
    }

    val _events = MutableSharedFlow<SearchEvent>()
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
            is SearchEvent.SubmitComponentsToMeal -> addComponentsToMeal()
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
                val foodProducts = foodProductRepository.searchFoodProduct(
                    query,
                    _searchState.value.language
                )
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

    private fun changeQuery(query: String) {
        _searchState.update { it.copy(query = query) }
    }

    private fun cancelSearch() {
        _searchState.update { it.copy(results = emptyList(), query = "") }
    }

    private fun changeDayTime(dayTime: DayTime) {
        _searchState.update { it.copy(dayTime = dayTime) }
    }

    private fun addComponentsToMeal() {
        val meal = Meal(
            id = UUID.randomUUID().toString(),
            historyDayDate = _searchState.value.date!!,
            dayTime = _searchState.value.dayTime!!
        )
        var mealFoodItemsWithProduct: List<Pair<Double, FoodProductEntity>>? = null
        var mealRecipeItemsWithRecipeEntity: List<Pair<Double, RecipeEntity>>? = null

        _searchState.value.addedComponents.forEach { component ->
            when (component) {
                is FoodProduct -> {
                    val foodProductEntity = DbFoodProductMapper.toFoodProductEntity(component)
                    mealFoodItemsWithProduct = (mealFoodItemsWithProduct ?: emptyList()) + Pair(
                        foodProductEntity.servings,
                        foodProductEntity
                    )
                }
                is Recipe -> {
                    val recipeEntity = DbRecipeMapper.toRecipeEntity(component)
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