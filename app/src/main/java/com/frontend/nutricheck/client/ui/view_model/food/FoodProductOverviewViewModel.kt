package com.frontend.nutricheck.client.ui.view_model.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val servings: Int = 1,
    val servingSize: ServingSize = ServingSize.ONEHOUNDREDGRAMS,
    val servingSizeDropDownExpanded: Boolean = false
)

data class FoodProductOverviewState (
    val mode: FoodProductOverviewMode,
    val foodProduct: FoodProduct,
    val mealId: String? = null,
    val recipeId: String? = null,
    val parameters: CommonFoodProductOverviewParams
) {
    fun submitFoodProduct(): Pair<Double, FoodProduct> {
        return Pair(
            parameters.servings * (parameters.servingSize.getAmount() / 100.0),
            foodProduct
            )
    }
}

sealed interface FoodProductOverviewEvent {
    data class ServingsChanged(val servings: Int) : FoodProductOverviewEvent
    data class ServingSizeChanged(val servingSize: ServingSize) : FoodProductOverviewEvent
    data object SaveAndAddClick : FoodProductOverviewEvent
    data object GoBack : FoodProductOverviewEvent
    data object ServingSizeDropDownClick : FoodProductOverviewEvent
}

@HiltViewModel
class FoodProductOverviewViewModel @Inject constructor(
    private val foodProductRepository: FoodProductRepository,
    private val recipeRepository: RecipeRepository,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : BaseFoodOverviewViewModel() {

    private val mode: FoodProductOverviewMode = savedStateHandle.run {
        val recipeId: String? = savedStateHandle.get<String>("recipeId")?.takeIf { it.isNotBlank() }
        val mealId = get<String>("mealId")
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
            val foodProduct = when (mode) {
                is FoodProductOverviewMode.FromSearch -> foodProductRepository.getFoodProductById(mode.foodProductId)
                is FoodProductOverviewMode.FromIngredient -> {
                    val ingredient = recipeRepository.getIngredientById(mode.recipeId, mode.foodProductId)
                    ingredient.foodProduct
                }
                is FoodProductOverviewMode.FromMeal -> {
                    val mealFoodItem = historyRepository.getMealFoodItemById(mode.mealId, mode.foodProductId)
                    mealFoodItem.foodProduct
                }
            }
            val newParams = initialParams.copy(
                foodName = foodProduct.name,
                calories = foodProduct.calories,
                protein = foodProduct.protein,
                carbohydrates = foodProduct.carbohydrates,
                fat = foodProduct.fat
            )
            _state.update { it.copy(foodProduct = foodProduct, parameters = newParams) }
        }
    }

    private val _events = MutableSharedFlow<FoodProductOverviewEvent>()
    val events: SharedFlow<FoodProductOverviewEvent> = _events.asSharedFlow()

    fun onEvent(event: FoodProductOverviewEvent) {
        when (event) {
            is FoodProductOverviewEvent.ServingsChanged -> onServingsChanged(event.servings)
            is FoodProductOverviewEvent.ServingSizeChanged -> onServingSizeChanged(event.servingSize)
            is FoodProductOverviewEvent.SaveAndAddClick -> viewModelScope.launch { onSaveChanges() }
            is FoodProductOverviewEvent.GoBack -> onBackClick()
            FoodProductOverviewEvent.ServingSizeDropDownClick -> onServingSizeDropDownClick()
        }
    }

    override suspend fun onSaveChanges() {
        val state = _state.value
        val commonParams = state.parameters

        when (state.mode) {
            is FoodProductOverviewMode.FromIngredient -> {
                val ingredient = Ingredient(
                    recipeId = state.recipeId!!,
                    foodProduct = state.foodProduct,
                    quantity = commonParams.servings * (commonParams.servingSize.getAmount() / 100.0)
                )
                recipeRepository.updateIngredient(ingredient)
                emitEvent(FoodProductOverviewEvent.GoBack)
            }
            is FoodProductOverviewMode.FromMeal -> {
                val mealFoodItem = MealFoodItem(
                    mealId = state.mealId!!,
                    foodProduct = state.foodProduct,
                    quantity = commonParams.servings * (commonParams.servingSize.getAmount() / 100.0))
                historyRepository.updateMealFoodItem(mealFoodItem)
                emitEvent(FoodProductOverviewEvent.GoBack)
            }
            is FoodProductOverviewMode.FromSearch -> {
            }
        }
    }

    override fun onBackClick() =
        emitEvent(FoodProductOverviewEvent.GoBack)

    override fun onServingsChanged(servings: Int) {
        _state.update { state ->
            state.copy(
                parameters = state.parameters.copy(servings = servings)
            )
        }
        convertNutrients()
    }

    override fun onServingSizeChanged(servingSize: ServingSize) {
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
            if (parameters.servingSize == ServingSize.ONEHOUNDREDGRAMS) {
                return@update state // No conversion needed for 100g serving size
            }
            val servings = parameters.servings
            val servingSize = parameters.servingSize.getAmount()
            state.copy(parameters = state.parameters.copy(
                calories = servings * servingSize * (parameters.calories / 100),
                protein = servings * servingSize * (parameters.protein / 100),
                carbohydrates = servings * servingSize * (parameters.carbohydrates / 100),
                fat = servings * servingSize * (parameters.fat / 100))
            )
        }
    }

    private fun emitEvent(event: FoodProductOverviewEvent) =
        viewModelScope.launch { _events.emit(event) }

}