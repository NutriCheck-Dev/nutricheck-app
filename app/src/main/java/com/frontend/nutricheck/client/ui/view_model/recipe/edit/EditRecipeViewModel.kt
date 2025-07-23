package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
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
    val ingredients: Set<Ingredient>,
    val addedIngredients: Set<Ingredient> = emptySet()
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
    data class IngredientAdded(val ingredients: FoodComponent) : EditRecipeEvent
    data class IngredientRemovedInSummary(val ingredient: FoodComponent) : EditRecipeEvent
    data class IngredientRemovedInEdit(val ingredient: FoodComponent) : EditRecipeEvent
    data class DescriptionChanged(val description: String) : EditRecipeEvent
    data object SaveAddedIngredients : EditRecipeEvent
    data object EditCanceled : EditRecipeEvent
    data object RecipeSaved : EditRecipeEvent
    data object SaveChanges : EditRecipeEvent
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
                            ingredients = recipe.ingredients
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
            is EditRecipeEvent.IngredientRemovedInSummary -> onIngredientRemovedInSummary(event.ingredient)
            is EditRecipeEvent.IngredientRemovedInEdit -> onIngredientRemovedInEdit(event.ingredient)
            is EditRecipeEvent.SaveAddedIngredients -> onSaveAddedIngredients()
            is EditRecipeEvent.EditCanceled -> onCancelEdit()
            is EditRecipeEvent.RecipeSaved -> onSaveRecipe()
            is EditRecipeEvent.SaveChanges -> saveChanges()
        }
    }

    override fun onTitleChanged(newTitle: String) =
        _editRecipeDraft.update { it?.copy(title = newTitle) }

    override fun onIngredientAdded(ingredient: FoodComponent) {
        val newIngredient = Ingredient(
            id = ingredient.id, //TODO: ids noch generieren lassen
            foodProductId = ingredient.id,
            foodProduct = ingredient as FoodProduct
        )
        _editRecipeDraft.update { it?.copy(addedIngredients = it.addedIngredients + newIngredient) }
    }

    override fun onIngredientRemovedInSummary(ingredient: FoodComponent) {
        _editRecipeDraft.update { draft ->
            draft?.copy(
                addedIngredients = draft.addedIngredients.filterNot { it.foodProductId == ingredient.id }.toSet()
            )
        }
    }

    override fun onIngredientRemovedInEdit(ingredient: FoodComponent) {
        _editRecipeDraft.update { draft ->
            draft?.copy(
                ingredients = draft.ingredients.filterNot { it.foodProductId == ingredient.id }.toSet()
            )
        }
    }

    override fun onSaveAddedIngredients() =
        _editRecipeDraft.update { it?.copy(ingredients = it.ingredients + it.addedIngredients) }

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
        val draft = _editRecipeDraft.value ?: return
        if (draft.title.isBlank()) {
            setError("Recipe title cannot be empty")
            return
        }
        if (draft.ingredients.isEmpty()) {
            setError("Recipe must have at least one ingredient")
            return
        }
        setReady()
        viewModelScope.launch {
            recipeRepository.updateRecipe(draft.toRecipe())
            _events.emit(EditRecipeEvent.RecipeSaved)
        }
    }
}