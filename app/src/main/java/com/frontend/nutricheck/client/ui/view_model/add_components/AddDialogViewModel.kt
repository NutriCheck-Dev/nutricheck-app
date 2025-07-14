package com.frontend.nutricheck.client.ui.view_model.add_components

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddDialogState(
    val isOpen: Boolean = false
)

sealed interface AddDialogEvent {
    data object AddMeal : AddDialogEvent
    data object ScanFood : AddDialogEvent
    data object AddRecipe : AddDialogEvent
}

@HiltViewModel
class AddDialogViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : BaseAddDialogViewModel() {
    private val _addDialogState = MutableStateFlow(AddDialogState())
    val createRecipeState = _addDialogState.asStateFlow()

    val _events = MutableSharedFlow<AddDialogEvent>()
    val events: SharedFlow<AddDialogEvent> = _events.asSharedFlow()

    fun onEvent(event: AddDialogEvent) {}

    override fun onAddMealClick() { emitEvent(AddDialogEvent.AddMeal) }

    override fun onScanFoodClick() { emitEvent(AddDialogEvent.ScanFood) }

    override fun onAddRecipeClick() { emitEvent(AddDialogEvent.AddRecipe) }

    override fun onDismiss() { closeDialog() }

    private fun emitEvent(event: AddDialogEvent) = viewModelScope.launch { _events.emit(event) }
}