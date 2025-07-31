package com.frontend.nutricheck.client.ui.view_model.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
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

sealed class FoodProductOverviewState {
    abstract val foodProduct: FoodProduct
    abstract val parameters: CommonFoodProductOverviewParams
    fun updateParams(params: CommonFoodProductOverviewParams): FoodProductOverviewState =
        when (this) {
            is IngredientState -> copy(parameters = params)
            is MealFoodItemState -> copy(parameters = params)
            is SearchState -> copy(parameters = params)
        }
    data class IngredientState(
        val ingredient: Ingredient,
        override val parameters: CommonFoodProductOverviewParams
    ) : FoodProductOverviewState() {
        override val foodProduct: FoodProduct get() = ingredient.foodProduct
    }

    data class MealFoodItemState(
        val mealFoodItem: MealFoodItem,
        override val parameters: CommonFoodProductOverviewParams
    ) : FoodProductOverviewState() {
        override val foodProduct: FoodProduct get() = mealFoodItem.foodProduct
    }

    data class SearchState(
        override val foodProduct: FoodProduct,
        override val parameters: CommonFoodProductOverviewParams,
    ) : FoodProductOverviewState() {
        /**fun submitFoodProduct(): Pair<Double, FoodProduct> =
            Pair(parameters.servings * (parameters.servingSize.getAmount()/100).toDouble(),
                foodProduct!!
            )**/
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
    private val foodProductRepository: FoodProductRepositoryImpl,
    private val recipeRepository: RecipeRepositoryImpl,
    private val historyRepository: HistoryRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : BaseFoodOverviewViewModel() {

    private lateinit var _state: MutableStateFlow<FoodProductOverviewState>
    val foodProductOverviewState: StateFlow<FoodProductOverviewState> get() =
        _state.asStateFlow()

    private val mode: FoodProductOverviewMode = savedStateHandle.run {
        val recipeId = get<String>("recipeId")
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

    init {
        viewModelScope.launch {
            when (mode) {
                is FoodProductOverviewMode.FromIngredient -> {
                    val ingredient = recipeRepository.getIngredientById(mode.recipeId, mode.foodProductId)
                    val commonParams = CommonFoodProductOverviewParams(
                        foodName = ingredient.foodProduct.name,
                        calories = ingredient.foodProduct.calories,
                        protein = ingredient.foodProduct.protein,
                        carbohydrates = ingredient.foodProduct.carbohydrates,
                        fat = ingredient.foodProduct.fat
                    )
                    FoodProductOverviewState.IngredientState(
                        ingredient = ingredient,
                        parameters = commonParams
                    )
                }
                is FoodProductOverviewMode.FromMeal -> {
                    val mealFoodItem = historyRepository.getMealFoodItemById(mode.mealId, mode.foodProductId)
                    val commonParams = CommonFoodProductOverviewParams(
                        foodName = mealFoodItem.foodProduct.name,
                        calories = mealFoodItem.foodProduct.calories,
                        protein = mealFoodItem.foodProduct.protein,
                        carbohydrates = mealFoodItem.foodProduct.carbohydrates,
                        fat = mealFoodItem.foodProduct.fat
                    )
                    FoodProductOverviewState.MealFoodItemState(
                        mealFoodItem = mealFoodItem,
                        parameters = commonParams
                    )
                }
                is FoodProductOverviewMode.FromSearch -> {
                    val foodProduct = foodProductRepository.getFoodProductById(mode.foodProductId)
                    val commonParams = CommonFoodProductOverviewParams(
                        foodName = foodProduct.name,
                        calories = foodProduct.calories,
                        protein = foodProduct.protein,
                        carbohydrates = foodProduct.carbohydrates,
                        fat = foodProduct.fat,
                    )
                    FoodProductOverviewState.SearchState(foodProduct = foodProduct, parameters = commonParams)
                }
            }
        }
    }

    val _events = MutableSharedFlow<FoodProductOverviewEvent>()
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

        when (state) {
            is FoodProductOverviewState.IngredientState -> {
                recipeRepository.updateIngredient(
                    state.ingredient.copy(
                        quantity = commonParams.servings * (commonParams.servingSize.getAmount() / 100.0)
                    ),
                )
                emitEvent(FoodProductOverviewEvent.GoBack)
            }
            is FoodProductOverviewState.MealFoodItemState -> {
                historyRepository.updateMealFoodItem(
                    state.mealFoodItem.copy(
                        quantity = commonParams.servings * (commonParams.servingSize.getAmount() / 100.0)
                    )
                )
                emitEvent(FoodProductOverviewEvent.GoBack)
            }
            is FoodProductOverviewState.SearchState -> {
                // Submit the food product and get the updated values
                emitEvent(FoodProductOverviewEvent.SaveAndAddClick)
            }
        }
    }

    override fun onBackClick() =
        emitEvent(FoodProductOverviewEvent.GoBack)

    override fun onServingsChanged(servings: Int) =
        _state.update { state ->
            val newParams = state.parameters.copy(servings = servings)
            state.updateParams(newParams)
        }

    override fun onServingSizeChanged(servingSize: ServingSize) =
        _state.update { state ->
            val newParams = state.parameters.copy(servingSize = servingSize)
            convertNutrients()
            state.updateParams(newParams)
        }

    private fun onServingSizeDropDownClick() =
        _state.update { state ->
            val newParams = state.parameters.copy(servingSizeDropDownExpanded = !state.parameters.servingSizeDropDownExpanded)
            state.updateParams(newParams)
        }

    private fun convertNutrients() {
        _state.update { state ->
            val parameters = state.parameters
            val servings = parameters.servings
            val servingSize = parameters.servingSize
            val newParams = parameters.copy(
                calories = servings * servingSize.getAmount() * (parameters.calories / 100),
                protein = servings * servingSize.getAmount() * (parameters.protein / 100),
                carbohydrates = servings * servingSize.getAmount() * (parameters.carbohydrates / 100),
                fat = servings * servingSize.getAmount() * (parameters.fat / 100)
            )
            state.updateParams(newParams)
        }
    }

    private fun emitEvent(event: FoodProductOverviewEvent) =
        viewModelScope.launch { _events.emit(event) }

}