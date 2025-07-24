package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeDraft(
    val id: String,
    val title: String,
    val description: String,
    val ingredients: List<Ingredient> = emptyList(),
    val addedIngredients: List<Ingredient> = emptyList(),
    val viewIngredients: List<FoodComponent> = emptyList(),
) {
    fun toRecipe(): Recipe = Recipe(
        id = id,
        name = title,
        instructions = description
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
    data object SaveChanges : EditRecipeEvent
}

@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val foodProductRepository: FoodProductRepository,
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
                            description = recipe.instructions
                        )
                    }
                }
            recipeRepository.getIngredientsForRecipe(recipeId)
                .collect { ingredients ->
                    val foodComponents = ingredients.map { ingredient ->
                        getFoodComponentOfIngredient(ingredient)
                    }
                    _editRecipeDraft.update {
                        it!!.copy(ingredients = ingredients, viewIngredients = foodComponents)
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
            is EditRecipeEvent.EditCanceled -> viewModelScope.launch { onCancelEdit() }
            is EditRecipeEvent.SaveChanges -> saveChanges()
        }
    }

    override fun onTitleChanged(newTitle: String) =
        _editRecipeDraft.update { it?.copy(title = newTitle) }

    override fun onIngredientAdded(foodComponent: FoodComponent) {
        val newIngredient = Ingredient(
            id = "", //TODO: ids noch generieren lassen,
            recipeId = recipeId,
            foodProductId = foodComponent.id,
            quantity = foodComponent.servings.toDouble()
        )
        _editRecipeDraft.update { it?.copy(addedIngredients = it.addedIngredients + newIngredient) }
    }

    override fun onIngredientRemovedInSummary(foodComponent: FoodComponent) {
        _editRecipeDraft.update { draft ->
            draft?.copy(
                addedIngredients = draft.addedIngredients.filterNot { it.foodProductId == foodComponent.id }
            )
        }
    }

    override fun onIngredientRemovedInEdit(foodComponent: FoodComponent) {
        _editRecipeDraft.update { draft ->
            draft?.copy(
                ingredients = draft.ingredients.filterNot { it.foodProductId == foodComponent.id }
            )
        }
    }

    override fun onSaveAddedIngredients() =
        _editRecipeDraft.update { it?.copy(ingredients = it.ingredients + it.addedIngredients) }

    override fun onDescriptionChanged(newDescription: String) =
        _editRecipeDraft.update { it?.copy(description = newDescription) }

    override suspend fun onCancelEdit() {
        _editRecipeDraft.value = _recipe.value!!.let { recipe ->
            RecipeDraft(
                id = recipeId,
                title = recipe.name,
                description = recipe.instructions,
                ingredients = recipeRepository.getIngredientsForRecipe(recipeId).first()
            )
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
            _events.emit(EditRecipeEvent.SaveChanges)
        }
    }

    private suspend fun getFoodComponentOfIngredient(ingredient: Ingredient) : FoodProduct =
        foodProductRepository.getFoodProductById(ingredient.foodProductId).first()
}