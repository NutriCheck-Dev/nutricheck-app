package com.frontend.nutricheck.client.ui.view_model.recipe.create

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.frontend.nutricheck.client.R
import kotlinx.coroutines.launch

data class CreatedRecipeDraft(
    val id: String,
    val title: String,
    val description: String,
    val ingredients: Set<Ingredient>
) {
    fun toRecipe(): Recipe = Recipe(
        id = id,
        name = title,
        description = description,
        ingredients = ingredients
    )
}

sealed interface CreateRecipeEvent {
    data class TitleChanged(val title: String) : CreateRecipeEvent
    data class IngredientAdded(val ingredients: Ingredient) : CreateRecipeEvent
    data class IngredientRemoved(val ingredient: Ingredient) : CreateRecipeEvent
    data class DescriptionChanged(val description: String) : CreateRecipeEvent
    data object EditCanceled : CreateRecipeEvent
    data object RecipeSaved : CreateRecipeEvent
}

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : BaseCreateRecipeViewModel() {

    private val _errorState = MutableStateFlow<Int?>(null)
    val errorState: StateFlow<Int?> = _errorState.asStateFlow()
    private val _createdRecipeDraft = MutableStateFlow<CreatedRecipeDraft?>(null)
    val createdRecipeDraft = _createdRecipeDraft.asStateFlow()

    val _events = MutableSharedFlow<CreateRecipeEvent>()
    val events: SharedFlow<CreateRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: CreateRecipeEvent) {
        when (event) {
            is CreateRecipeEvent.TitleChanged -> onTitleAdded(event.title)
            is CreateRecipeEvent.IngredientAdded -> onIngredientAdded(event.ingredients)
            is CreateRecipeEvent.IngredientRemoved -> onIngredientRemoved(event.ingredient)
            is CreateRecipeEvent.DescriptionChanged -> onDescriptionAdded(event.description)
            is CreateRecipeEvent.EditCanceled -> onCancelCreation()
            is CreateRecipeEvent.RecipeSaved -> onSaveRecipe()
        }
    }

    override fun onTitleAdded(newTitle: String) {
        if (newTitle.isBlank()) {
            _errorState.value = R.string.onboarding_error_name_required
            return
        }
        _errorState.value = null
        _createdRecipeDraft.update { it?.copy(title = newTitle) }
    }

    override fun onIngredientAdded(ingredient: Ingredient) =
        _createdRecipeDraft.update { it?.copy(ingredients = it.ingredients + ingredient) }

    override fun onIngredientRemoved(ingredient: Ingredient) =
        _createdRecipeDraft.update { it?.copy(ingredients = it.ingredients - ingredient) }

    override fun onDescriptionAdded(newDescription: String) =
        _createdRecipeDraft.update { it?.copy(description = newDescription) }

    override fun onCancelCreation() {
        _createdRecipeDraft.value = null
    }

    override fun onSaveRecipe() {
        val createdRecipe = _createdRecipeDraft.value!!.toRecipe()
        viewModelScope.launch {
            recipeRepository.insertRecipe(createdRecipe)
        }
    }


}