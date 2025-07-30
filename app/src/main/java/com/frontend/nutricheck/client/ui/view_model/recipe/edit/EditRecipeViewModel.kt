package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDraft(
    val recipe: Recipe? = null,
    val id: String,
    val title: String,
    val description: String,
    val ingredients: List<Ingredient> = emptyList(),
    val addedIngredient: List<Ingredient> = emptyList(),
    val viewIngredients: List<FoodComponent> = emptyList()
)

sealed interface EditRecipeEvent {
    data class TitleChanged(val title: String) : EditRecipeEvent
    data class IngredientAdded(val foodProduct: FoodProduct) : EditRecipeEvent
    data class IngredientRemovedInSummary(val foodProduct: FoodProduct) : EditRecipeEvent
    data class IngredientRemovedInEdit(val foodProduct: FoodProduct) : EditRecipeEvent
    data class DescriptionChanged(val description: String) : EditRecipeEvent
    data object SaveAddedIngredients : EditRecipeEvent
    data object EditCanceled : EditRecipeEvent
    data object SaveChanges : EditRecipeEvent
}

@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : BaseEditRecipeViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"]) {
        "Missing recipeId"
    }

    private val _editRecipeDraft = MutableStateFlow<RecipeDraft?>(null)
    val editRecipeDraft: StateFlow<RecipeDraft?> = _editRecipeDraft.asStateFlow()

    init {
        viewModelScope.launch {
            val recipe = recipeRepository.getRecipeById(recipeId)
            _editRecipeDraft.update { it!!.copy(
                recipe = recipe,
                id = recipeId,
                title = recipe.name,
                ingredients = recipe.ingredients,
                description = recipe.instructions,
            ) }
        }
    }

    private val _events = MutableSharedFlow<EditRecipeEvent>()
    val events: SharedFlow<EditRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: EditRecipeEvent) {
        when (event) {
            is EditRecipeEvent.TitleChanged -> onTitleChanged(event.title)
            is EditRecipeEvent.IngredientAdded -> onIngredientAdded(event.foodProduct)
            is EditRecipeEvent.DescriptionChanged -> onDescriptionChanged(event.description)
            is EditRecipeEvent.IngredientRemovedInSummary -> onIngredientRemovedInSummary(event.foodProduct)
            is EditRecipeEvent.IngredientRemovedInEdit -> onIngredientRemovedInEdit(event.foodProduct)
            is EditRecipeEvent.SaveAddedIngredients -> onSaveAddedIngredients()
            is EditRecipeEvent.EditCanceled -> viewModelScope.launch { onCancelEdit() }
            is EditRecipeEvent.SaveChanges -> saveChanges()
        }
    }

    override fun onTitleChanged(newTitle: String) =
        _editRecipeDraft.update { it?.copy(title = newTitle) }

    override fun onIngredientAdded(foodProduct: FoodProduct) {
        val recipeId = _editRecipeDraft.value!!.id
        val newIngredient = Ingredient(
            recipeId = recipeId,
            foodProduct = foodProduct,
            quantity = 1.0,
        )
        _editRecipeDraft.update { it?.copy(addedIngredient = it.addedIngredient + newIngredient) }
    }

    override fun onIngredientRemovedInSummary(foodProduct: FoodProduct) {
        _editRecipeDraft.update { draft ->
            draft?.copy(
                addedIngredient = draft.addedIngredient.filterNot { it.foodProduct.id == foodProduct.id }
            )
        }
    }

    override fun onIngredientRemovedInEdit(foodProduct: FoodProduct) {
        _editRecipeDraft.update { draft ->
            draft?.copy(
                ingredients = draft.ingredients.filterNot { it.foodProduct.id == foodProduct.id },
                viewIngredients = draft.viewIngredients.filterNot { it.id == foodProduct.id }
            )
        }
    }

    override fun onSaveAddedIngredients() =
        _editRecipeDraft.update { it?.copy(ingredients = it.ingredients + it.addedIngredient) }

    override fun onDescriptionChanged(newDescription: String) =
        _editRecipeDraft.update { it?.copy(description = newDescription) }

    override suspend fun onCancelEdit() {
        _editRecipeDraft.value =
            RecipeDraft(
                id = recipeId,
                title = _editRecipeDraft.value!!.recipe.name,
                description = _editRecipeDraft.value!!.recipe.instructions,
                ingredients = _editRecipeDraft.value!!.recipe.ingredients,
                addedIngredient = emptyList(),
                viewIngredients = _editRecipeDraft.value!!.recipe.ingredients.map { it.foodProduct },
                recipe = _editRecipeDraft.value!!.recipe
            )

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
        val nutritionalValues = calculateNutritionalValues(draft.ingredients)
        val recipe = Recipe(
            id = draft.id,
            name = draft.title,
            calories = nutritionalValues["calories"]!!,
            carbohydrates = nutritionalValues["carbohydrates"]!!,
            protein = nutritionalValues["protein"]!!,
            fat = nutritionalValues["fat"]!!,
            servings = draft.ingredients.sumOf { it.quantity.toInt() },
            ingredients = draft.ingredients,
            instructions = draft.description,
            visibility = draft.recipe.visibility
        )
        viewModelScope.launch {
            recipeRepository.updateRecipe(recipe)
        }
    }

    private fun calculateNutritionalValues(ingredients: List<Ingredient>): Map<String, Double> {
        return ingredients.fold(mutableMapOf()) { map, ingredient ->
            map.apply {
                put("calories", (get("calories")!!) + ingredient.foodProduct.calories)
                put("carbohydrates", (get("carbohydrates")!!) + ingredient.foodProduct.carbohydrates)
                put("protein", (get("protein")!!) + ingredient.foodProduct.protein)
                put("fat", (get("fat")!!) + ingredient.foodProduct.fat)
            }
        }
    }
}