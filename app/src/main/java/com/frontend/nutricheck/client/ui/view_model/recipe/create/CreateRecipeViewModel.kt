package com.frontend.nutricheck.client.ui.view_model.recipe.create

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreatedRecipeDraft(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val addedIngredients: List<Ingredient> = emptyList(),
    val viewIngredients: List<FoodProduct> = emptyList()
) {
    fun toRecipe(): Recipe = Recipe(
        id = id,
        name = title,
        instructions = description
    )
}

sealed interface CreateRecipeEvent {
    data class TitleChanged(val title: String) : CreateRecipeEvent
    data class IngredientAdded(val foodProduct: FoodProduct) : CreateRecipeEvent
    data class IngredientRemovedInSummary(val foodProduct: FoodProduct) : CreateRecipeEvent
    data class IngredientRemovedInCreation(val foodProduct: FoodProduct) : CreateRecipeEvent
    data class DescriptionChanged(val description: String) : CreateRecipeEvent
    data object SaveAddedIngredients : CreateRecipeEvent
    data object EditCanceled : CreateRecipeEvent
    data object RecipeSaved : CreateRecipeEvent
}

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepositoryImpl,
    private val foodProductRepository: FoodProductRepositoryImpl
) : BaseCreateRecipeViewModel() {

    private val _errorState = MutableStateFlow<Int?>(null)
    val errorState: StateFlow<Int?> = _errorState.asStateFlow()
    private val _createdRecipeDraft = MutableStateFlow(CreatedRecipeDraft())
    val createdRecipeDraft = _createdRecipeDraft.asStateFlow()

    val _events = MutableSharedFlow<CreateRecipeEvent>()
    val events: SharedFlow<CreateRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: CreateRecipeEvent) {
        when (event) {
            is CreateRecipeEvent.TitleChanged -> onTitleAdded(event.title)
            is CreateRecipeEvent.IngredientAdded -> onIngredientAdded(event.foodProduct)
            is CreateRecipeEvent.IngredientRemovedInSummary -> onIngredientRemovedInSummary(event.foodProduct)
            is CreateRecipeEvent.IngredientRemovedInCreation -> onIngredientRemovedInCreation(event.foodProduct)
            is CreateRecipeEvent.DescriptionChanged -> onDescriptionAdded(event.description)
            is CreateRecipeEvent.SaveAddedIngredients -> viewModelScope.launch {
                onSaveAddedIngredients()
            }
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
        _createdRecipeDraft.update { it.copy(title = newTitle) }
    }

    override fun onIngredientAdded(foodProduct: FoodProduct) {
        val newIngredient = Ingredient(
            id = UUID.randomUUID().toString(),
            recipeId = _createdRecipeDraft.value.id,
            foodProductId = foodProduct.id,
            quantity = foodProduct.servings.toDouble()
        )
        _createdRecipeDraft.update { it.copy(addedIngredients = it.addedIngredients + newIngredient) }
    }

    override fun onIngredientRemovedInSummary(foodProduct: FoodProduct) {
        _createdRecipeDraft.update { draft ->
            draft.copy(
                addedIngredients = draft.addedIngredients.filterNot { it.foodProductId == foodProduct.id }
            )
        }
    }

    override fun onIngredientRemovedInCreation(foodProduct: FoodProduct) {
        _createdRecipeDraft.update { draft ->
            draft.copy(
                ingredients = draft.ingredients.filterNot { it.foodProductId == foodProduct.id },
                viewIngredients = draft.viewIngredients.filterNot { it.id == foodProduct.id }
            )
        }
    }

    override suspend fun onSaveAddedIngredients() =
        _createdRecipeDraft.update { draft ->
            draft.copy(
                ingredients = draft.ingredients + draft.addedIngredients,
                addedIngredients = emptyList(),
                viewIngredients = draft.viewIngredients
                        + draft.addedIngredients.map { getFoodComponentOfIngredient(it) }
            )
        }

    override fun onDescriptionAdded(newDescription: String) =
        _createdRecipeDraft.update { it.copy(description = newDescription) }

    override fun onCancelCreation() {
        _createdRecipeDraft.value = CreatedRecipeDraft()
    }

    fun onSaveRecipe() {
        val createdRecipe = _createdRecipeDraft.value
        if (createdRecipe.title.isBlank()) {
            setError("Recipe title cannot be empty")
            return
        }
        createdRecipe.ingredients.let {
            if (it.isEmpty()) {
                _errorState.value = R.string.create_recipe_error_ingredients
                return
            }
        }
        _errorState.value = null
        val recipe = createdRecipe.toRecipe()
        viewModelScope.launch {
            recipeRepository.insertRecipe(recipe)
        }
    }

    private suspend fun getFoodComponentOfIngredient(ingredient: Ingredient) : FoodProduct =
        foodProductRepository.getFoodProductById(ingredient.foodProductId).first()
}