package com.frontend.nutricheck.client.ui.view_model.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.ServingSize
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class FoodProductOverviewState(
    val foodProduct: FoodProduct = FoodProduct(),
    val foodName: String = "",
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fat: Double = 0.0,
    val servings: Int = 1,
    val servingSize: ServingSize = ServingSize.ONEHOUNDREDGRAMS,
    val servingSizeDropDownExpanded: Boolean = false,
    val isFromIngredient: Boolean = false
)

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
    savedStateHandle: SavedStateHandle
) : BaseFoodOverviewViewModel() {

    private val _foodProductOverviewState = MutableStateFlow(FoodProductOverviewState())
    val foodProductOverviewState = _foodProductOverviewState.asStateFlow()

    private val foodProductId: String = checkNotNull(savedStateHandle["foodId"]) {
        "Missing recipeId in savedStateHandle"
    }

    init {
        viewModelScope.launch {
            foodProductRepository.getFoodProductById(foodProductId)
                .collect { foodProduct ->
                    _foodProductOverviewState.update {
                        it.copy(
                            foodProduct = foodProduct,
                            foodName = foodProduct.name,
                            calories = foodProduct.calories,
                            protein = foodProduct.protein,
                            carbohydrates = foodProduct.carbohydrates,
                            fat = foodProduct.fat,
                            servings = foodProduct.servings,
                            servingSize = foodProduct.servingSize
                        ) }
                }
        }
    }

    val _events = MutableSharedFlow<FoodProductOverviewEvent>()
    val events: SharedFlow<FoodProductOverviewEvent> = _events.asSharedFlow()

    fun onEvent(event: FoodProductOverviewEvent) {
        when (event) {
            is FoodProductOverviewEvent.ServingsChanged -> onServingsChanged(event.servings)
            is FoodProductOverviewEvent.ServingSizeChanged -> onServingSizeChanged(event.servingSize)
            is FoodProductOverviewEvent.SaveAndAddClick -> onSaveAndAddClick()
            is FoodProductOverviewEvent.GoBack -> onBackClick()
            FoodProductOverviewEvent.ServingSizeDropDownClick -> onServingSizeDropDownClick()
        }
    }

    override fun onSaveAndAddClick(): FoodProduct {
        emitEvent(FoodProductOverviewEvent.SaveAndAddClick)
        return FoodProduct(
            id = UUID.randomUUID().toString(),
            name = _foodProductOverviewState.value.foodName,
            servings = _foodProductOverviewState.value.servings,
            servingSize = _foodProductOverviewState.value.servingSize,
            calories = _foodProductOverviewState.value.calories,
            protein = _foodProductOverviewState.value.protein,
            carbohydrates = _foodProductOverviewState.value.carbohydrates,
            fat = _foodProductOverviewState.value.fat
        )
    }

    override fun onBackClick() {
        _foodProductOverviewState.update { FoodProductOverviewState() }
    }

    override fun onServingsChanged(servings: Int) {
        _foodProductOverviewState.update { it.copy(servings = servings) }
        convertNutrients()
        emitEvent(FoodProductOverviewEvent.GoBack)
    }

    override fun onServingSizeChanged(servingSize: ServingSize) {
        _foodProductOverviewState.update { it.copy(servingSize = servingSize) }
        convertNutrients()
    }

    private fun onServingSizeDropDownClick() {
        _foodProductOverviewState.update {
            it.copy(servingSizeDropDownExpanded = !it.servingSizeDropDownExpanded)
        }
    }

    private fun convertNutrients() {
        val state = _foodProductOverviewState.value
        val servingSize = state.servingSize
        _foodProductOverviewState.update {
            it.copy(
                calories = state.servings * servingSize.getAmount() * (state.calories / 100),
                protein = state.servings * servingSize.getAmount() * (state.protein / 100),
                carbohydrates = state.servings * servingSize.getAmount() * (state.carbohydrates / 100),
                fat = state.servings * servingSize.getAmount() * (state.fat / 100)
            )
        }
    }

    private fun emitEvent(event: FoodProductOverviewEvent) =
        viewModelScope.launch { _events.emit(event) }

}