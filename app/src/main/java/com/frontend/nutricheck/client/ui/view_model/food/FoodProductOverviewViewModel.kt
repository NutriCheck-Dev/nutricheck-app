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
    val servings: Int = 1,
    val servingSize: ServingSize = ServingSize.ONEHOUNDREDGRAMS
)

sealed interface FoodProductOverviewEvent {
    data class ServingsChanged(val servings: Int) : FoodProductOverviewEvent
    data class ServingSizeChanged(val servingSize: ServingSize) : FoodProductOverviewEvent
    data object SaveAndAddClick : FoodProductOverviewEvent
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
        }
    }

    override fun onSaveAndAddClick(): FoodProduct {
        return FoodProduct(
            id = UUID.randomUUID().toString(),
            name = _foodProductOverviewState.value.foodName,
            servings = _foodProductOverviewState.value.servings,
            servingSize = _foodProductOverviewState.value.servingSize,

        )
    }

    override fun onServingsChanged(servings: Int) =
        _foodProductOverviewState.update { it.copy(servings = servings) }

    override fun onServingSizeChanged(servingSize: ServingSize) =
        _foodProductOverviewState.update {
            it.copy(servingSize = servingSize)
        }
}