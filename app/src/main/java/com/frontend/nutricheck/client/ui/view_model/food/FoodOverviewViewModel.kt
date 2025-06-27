package com.frontend.nutricheck.client.ui.view_model.food

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view_model.recipe.create.CreateRecipeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


sealed interface FoodOverviewEvent {
    data class AddToMealClick(val id: String) : FoodOverviewEvent
    data object OnEditClick : FoodOverviewEvent
}

@HiltViewModel
class FoodOverviewViewModel @Inject constructor() : BaseFoodOverviewViewModel<FoodProduct>(
    initialDraft = FoodProduct()
) {

    val title = draft.map { it.name }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val calories = draft.map { it.calories }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val protein = draft.map { it.protein }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val carbs = draft.map { it.carbohydrates }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val fat = draft.map { it.fat }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val _events = MutableSharedFlow<CreateRecipeEvent>()
    val events: SharedFlow<CreateRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: CreateRecipeEvent) {}

    override fun addToMealClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onEditClick() {
        TODO("Not yet implemented")
    }


}