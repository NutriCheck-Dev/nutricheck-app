package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class EditRecipeState(
    val title: String = "",
    val ingredients: Set<Ingredient> = emptySet(),
    val description: String = "",
    val calories: Int = 0,
    val carbs: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0
)

sealed interface EditRecipeEvent {
    data class TitleChanged(val title: String) : EditRecipeEvent
    data class IngredientsChanged(val ingredients: List<String>) : EditRecipeEvent
    data class DescriptionChanged(val description: String) : EditRecipeEvent
    data class EnergyInKcalChanged(val energyInKcal: Int) : EditRecipeEvent
}

@HiltViewModel
class EditRecipeViewModel @Inject constructor() : BaseEditRecipeViewModel<Recipe>(
    initialDraft = Recipe()
) {

    private val _editRecipeState = MutableStateFlow(EditRecipeState())
    val createRecipeState = _editRecipeState.asStateFlow()

    val _events = MutableSharedFlow<EditRecipeEvent>()
    val events: SharedFlow<EditRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: EditRecipeEvent) {}

    override fun updateDraft(newDraft: Recipe) {
        TODO("Not yet implemented")
    }

    override fun resetDraft() {
        TODO("Not yet implemented")
    }

    override fun addIngredient(ingredientId: String) {
        // Logic to add an ingredient to the recipe draft
    }

    override fun removeIngredient(ingredientId: String) {
        // Logic to remove an ingredient from the recipe draft
    }

    override fun saveChanges() {
        // Logic to save changes made to the recipe draft
    }
}