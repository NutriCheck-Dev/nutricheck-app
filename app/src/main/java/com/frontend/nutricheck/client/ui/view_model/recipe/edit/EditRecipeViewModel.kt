package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeDraft(
    val id: String,
    val title: String,
    val description: String,
    val ingredients: Set<Ingredient>
) {
    fun toRecipe(): Recipe = Recipe(
        id = id,
        name = title,
        instructions = description,
        ingredients = ingredients,
    )
}

sealed interface EditRecipeEvent {
    data class TitleChanged(val title: String) : EditRecipeEvent
    data class IngredientAdded(val ingredients: Ingredient) : EditRecipeEvent
    data class IngredientRemoved(val ingredient: Ingredient) : EditRecipeEvent
    data class DescriptionChanged(val description: String) : EditRecipeEvent
    data object EditCanceled : EditRecipeEvent
    data object RecipeSaved : EditRecipeEvent
}

@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : BaseEditRecipeViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"]) {
        "Missing recipeId"
    }

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    private val _editRecipeDraft = MutableStateFlow<RecipeDraft?>(null)
    val editRecipeDraft: StateFlow<RecipeDraft?> = _editRecipeDraft.asStateFlow()

    init {
        viewModelScope.launch {
            recipeRepository.getRecipeById(recipeId)
                .collect { recipe ->
                    _recipe.value = recipe
                    if (_editRecipeDraft.value == null) {
                        _editRecipeDraft.value = RecipeDraft(
                            id = recipe.id,
                            title = recipe.name,
                            description = recipe.instructions,
                            ingredients = recipe.ingredients.toSet()
                        )
                    }
                }
        }
    }

    val _events = MutableSharedFlow<EditRecipeEvent>()
    val events: SharedFlow<EditRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: EditRecipeEvent) {
        when (event) {
            is EditRecipeEvent.TitleChanged -> onTitleChanged(event.title)
            is EditRecipeEvent.IngredientAdded -> onIngredientAdded(event.ingredients)
            is EditRecipeEvent.DescriptionChanged -> onDescriptionChanged(event.description)
            is EditRecipeEvent.IngredientRemoved -> onIngredientRemoved(event.ingredient)
            is EditRecipeEvent.EditCanceled -> onCancelEdit()
            is EditRecipeEvent.RecipeSaved -> onSaveRecipe()
        }
    }

    override fun onTitleChanged(newTitle: String) =
        _editRecipeDraft.update { it?.copy(title = newTitle) }

    override fun onIngredientAdded(ingredient: Ingredient) =
        _editRecipeDraft.update { it?.copy(ingredients = it.ingredients + ingredient) }

    override fun onIngredientRemoved(ingredient: Ingredient) =
        _editRecipeDraft.update { it?.copy(ingredients = it.ingredients - ingredient) }

    override fun onDescriptionChanged(newDescription: String) =
        _editRecipeDraft.update { it?.copy(description = newDescription) }

    override fun onCancelEdit() {
        _editRecipeDraft.value = _recipe.value!!.let { recipe ->
            RecipeDraft(
                id = recipe.id,
                title = recipe.name,
                description = recipe.instructions,
                ingredients = recipe.ingredients
            )
        }
    }

    override fun onSaveRecipe() {
        val updatedRecipe = _editRecipeDraft.value!!.toRecipe()
        viewModelScope.launch {
            recipeRepository.updateRecipe(updatedRecipe)
            _recipe.value = updatedRecipe
        }
    }

    fun saveChanges() {
        // Logic to save changes made to the recipe draft
    }
}