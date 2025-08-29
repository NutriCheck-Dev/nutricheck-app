package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.utils.CombinedSearchListStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FoodProductOverviewMode {
    data class FromIngredient(val recipeId: String, val foodProductId: String) : FoodProductOverviewMode()
    data class FromMeal(val mealId: String, val foodProductId: String) : FoodProductOverviewMode()
    data class FromSearch(val foodProductId: String) : FoodProductOverviewMode()
}

data class CommonFoodProductOverviewParams(
    val foodName: String = "",
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fat: Double = 0.0,
    val servings: Double = 1.0,
    val servingSize: ServingSize = ServingSize.ONEHOUNDREDGRAMS,
    val servingSizeDropDownExpanded: Boolean = false,
    val editable: Boolean = true
)

data class FoodProductOverviewState (
    val mode: FoodProductOverviewMode,
    val foodProduct: FoodProduct,
    val mealId: String? = null,
    val recipeId: String? = null,
    val parameters: CommonFoodProductOverviewParams
) {
    fun submitFoodProduct(): FoodProduct {
        return foodProduct.copy(
            servings = parameters.servings,
            servingSize = parameters.servingSize,
        )
    }
}

sealed interface FoodProductOverviewEvent {
    data class ServingsChanged(val servings: Double) : FoodProductOverviewEvent
    data class ServingSizeChanged(val servingSize: ServingSize) : FoodProductOverviewEvent
    data object SaveAndAddClick : FoodProductOverviewEvent
    data object ServingSizeDropDownClick : FoodProductOverviewEvent
    data object DeleteAiMeal : FoodProductOverviewEvent
    data object SubmitMealItem : FoodProductOverviewEvent
    data object UpdateIngredient : FoodProductOverviewEvent
}

/**
 * ViewModel for managing the overview of a food product.
 * This ViewModel handles different modes of viewing a food product,
 * such as from a recipe ingredient, a meal, or a search result.
 * It provides functionality to update servings, serving sizes,
 * and save changes to the food product.
 *
 * @property foodProductRepository Repository for accessing food product data.
 * @property recipeRepository Repository for accessing recipe data.
 * @property historyRepository Repository for accessing meal history data.
 * @property combinedSearchListStore Store for managing the combined search list state.
 * @param savedStateHandle Saved state handle for managing state across configuration changes.
 */
@HiltViewModel
class FoodProductOverviewViewModel @Inject constructor(
    private val foodProductRepository: FoodProductRepository,
    private val recipeRepository: RecipeRepository,
    private val historyRepository: HistoryRepository,
    private val combinedSearchListStore: CombinedSearchListStore,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val mode: FoodProductOverviewMode = savedStateHandle.run {
        val recipeId: String? = savedStateHandle.get<String>("recipeId")?.takeIf {it.isNotBlank()}
        val mealId: String? = savedStateHandle.get<String>("mealId")?.takeIf { it.isNotBlank() }
        val foodProductId = get<String>("foodProductId")

        when {
            recipeId != null && foodProductId != null ->
                FoodProductOverviewMode.FromIngredient(
                    recipeId = recipeId,
                    foodProductId = foodProductId
                )
            mealId != null && foodProductId != null ->
                FoodProductOverviewMode.FromMeal(
                    mealId = mealId,
                    foodProductId = foodProductId
                )
            foodProductId != null ->
                FoodProductOverviewMode.FromSearch(foodProductId = foodProductId)
            else -> { throw IllegalArgumentException("Invalid state arguments for FoodProductOverviewViewModel") } //Temporary solution
        }
    }
    val editable: Boolean = savedStateHandle.get<String>("editable")?.toBoolean() ?: true
    private val initialParams = CommonFoodProductOverviewParams()
    private val initialFoodProduct = FoodProduct()
    private val initialState = FoodProductOverviewState(
        mode = mode,
        foodProduct = initialFoodProduct,
        parameters = initialParams
    )

    private var _state = MutableStateFlow(initialState)
    val foodProductViewState: StateFlow<FoodProductOverviewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val (foodProduct, servingsPair) = when (mode) {
                is FoodProductOverviewMode.FromSearch -> {
                    val foodProduct = combinedSearchListStore.state.first()
                        .find { it.id == mode.foodProductId }
                        ?: foodProductRepository.getFoodProductById(mode.foodProductId)
                    Pair(foodProduct, Pair(foodProduct.servings, (foodProduct as FoodProduct).servingSize))
                }

                is FoodProductOverviewMode.FromIngredient -> {
                    val ingredient =
                        recipeRepository.getIngredientById(mode.recipeId, mode.foodProductId)
                    _state.update { it.copy(recipeId = mode.recipeId) }
                    Pair(ingredient.foodProduct, Pair(ingredient.servings, ingredient.servingSize))
                }

                is FoodProductOverviewMode.FromMeal -> {
                    val mealFoodItem =
                        historyRepository.getMealFoodItemById(mode.mealId, mode.foodProductId)
                    _state.update { it.copy(mealId = mode.mealId) }
                    Pair(mealFoodItem.foodProduct, Pair(mealFoodItem.servings, mealFoodItem.servingSize))
                }
            }
            val newParams = initialParams.copy(
                foodName = foodProduct.name,
                calories = foodProduct.calories,
                protein = foodProduct.protein,
                carbohydrates = foodProduct.carbohydrates,
                fat = foodProduct.fat,
                servings = servingsPair.first,
                servingSize = servingsPair.second,
                editable = editable,
            )
            _state.update { it.copy(foodProduct = (foodProduct as FoodProduct), parameters = newParams) }
            convertNutrients()
        }

    }

    private val _events = MutableSharedFlow<FoodProductOverviewEvent>()
    val events: SharedFlow<FoodProductOverviewEvent> = _events.asSharedFlow()

    fun onEvent(event: FoodProductOverviewEvent) {
        when (event) {
            is FoodProductOverviewEvent.ServingsChanged -> onServingsChanged(event.servings)
            is FoodProductOverviewEvent.ServingSizeChanged -> onServingSizeChanged(event.servingSize)
            is FoodProductOverviewEvent.SaveAndAddClick -> viewModelScope.launch { onSaveChanges() }
            is FoodProductOverviewEvent.ServingSizeDropDownClick -> onServingSizeDropDownClick()
            is FoodProductOverviewEvent.DeleteAiMeal -> deleteAiMeal()
            else -> { /* Other Events are not handled here */ }
        }
    }

    private suspend fun onSaveChanges() {
        val state = _state.value
        val commonParams = state.parameters

        when (state.mode) {
            is FoodProductOverviewMode.FromIngredient -> {
                val ingredient = Ingredient(
                    recipeId = state.recipeId!!,
                    foodProduct = state.foodProduct,
                    quantity = commonParams.servings * commonParams.servingSize.getAmount(),
                    servings = commonParams.servings,
                    servingSize = commonParams.servingSize
                )
                recipeRepository.updateIngredient(ingredient)
                _events.emit(FoodProductOverviewEvent.UpdateIngredient)
            }
            is FoodProductOverviewMode.FromMeal -> {
                val mealFoodItem = MealFoodItem(
                    mealId = state.mealId!!,
                    foodProduct = state.foodProduct,
                    quantity = commonParams.servings * commonParams.servingSize.getAmount(),
                    servings = commonParams.servings,
                    servingSize = commonParams.servingSize
                )
                historyRepository.updateMealFoodItem(mealFoodItem)
                _events.emit(FoodProductOverviewEvent.SubmitMealItem)
            }
            is FoodProductOverviewMode.FromSearch -> { /* other options are not used here */ }
        }
    }

    private fun onServingsChanged(servings: Double) {
        _state.update { state ->
            state.copy(
                parameters = state.parameters.copy(servings = servings)
            )
        }
        convertNutrients()
    }

    private fun onServingSizeChanged(servingSize: ServingSize) {
        _state.update { state ->
            state.copy(
                parameters = state.parameters.copy(servingSize = servingSize)
            )
        }
        convertNutrients()
    }

    private fun onServingSizeDropDownClick() =
        _state.update { state ->
            state.copy(
                parameters = state.parameters.copy(
                    servingSizeDropDownExpanded = !state.parameters.servingSizeDropDownExpanded
                )
            )
        }

    private fun convertNutrients() {
        _state.update { state ->
            val parameters = state.parameters
            val initialFoodProduct = state.foodProduct
            val servings = parameters.servings
            val servingSize = parameters.servingSize.getAmount()
            state.copy(parameters = state.parameters.copy(
                calories = servings * servingSize * (initialFoodProduct.calories / 100),
                protein = servings * servingSize * (initialFoodProduct.protein / 100),
                carbohydrates = servings * servingSize * (initialFoodProduct.carbohydrates / 100),
                fat = servings * servingSize * (initialFoodProduct.fat / 100))
            )
        }
    }

    private fun deleteAiMeal() {
        viewModelScope.launch {
            when(mode) {
                is FoodProductOverviewMode.FromMeal -> {
                    val meal = historyRepository.getMealById(mode.mealId)
                    historyRepository.deleteMeal(meal)
                }
                else -> { /* Other options are not used here */ }
            }
        }
    }
}