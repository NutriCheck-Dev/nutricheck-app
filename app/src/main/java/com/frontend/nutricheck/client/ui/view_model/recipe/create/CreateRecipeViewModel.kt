package com.frontend.nutricheck.client.ui.view_model.recipe.create

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class CreateRecipeState(
    val draft: Recipe = Recipe(),
    val isValid: Boolean = false,
    val isSaving: Boolean = false
)

sealed interface CreateRecipeEvent {
    data class AddIngredient(val ingredientId: String) : CreateRecipeEvent
    data class RemoveIngredient(val ingredientId: String) : CreateRecipeEvent
    data object SaveRecipe : CreateRecipeEvent
    data object DiscardDraft : CreateRecipeEvent
    data object IngredientClick : CreateRecipeEvent
}

@HiltViewModel
class CreateRecipeViewModel @Inject constructor() : BaseCreateRecipeViewModel() {
    private val _createRecipeState = MutableStateFlow(CreateRecipeState())
    val createRecipeState = _createRecipeState.asStateFlow()

    val _events = MutableSharedFlow<CreateRecipeEvent>()
    val events: SharedFlow<CreateRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: CreateRecipeEvent) {}

    override fun validate(draft: Recipe): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun persistDraft(draft: Recipe): Result<Unit> {
        TODO("Not yet implemented")
    }
    override fun onClickAddIngredient() {
        // Logic to add an ingredient to the recipe draft
    }
    override fun onClickRemoveIngredient(ingredientId: String) {
        // Logic to remove an ingredient from the recipe draft
    }
    override fun onClickSaveRecipe() {
        // Logic to save the recipe draft
    }
    override fun onClickDiscardDraft() {
        // Logic to discard the recipe draft
    }

    override fun onIngredientClick() {
        TODO("Not yet implemented")
    }
   

}