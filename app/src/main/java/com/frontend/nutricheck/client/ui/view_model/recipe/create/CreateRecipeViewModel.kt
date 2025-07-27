package com.frontend.nutricheck.client.ui.view_model.recipe.create

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeVisibility
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
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
import java.util.UUID
import javax.inject.Inject

data class CreatedRecipeDraft(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val ingredient: List<Ingredient> = emptyList(),
    val addedIngredient: List<Ingredient> = emptyList(),
    val viewIngredients: List<FoodProduct> = emptyList()
)

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

    private val _events = MutableSharedFlow<CreateRecipeEvent>()
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
            _errorState.value = R.string.userData_error_name_required
            return
        }
        _errorState.value = null
        _createdRecipeDraft.update { it.copy(title = newTitle) }
    }

    override fun onIngredientAdded(foodProduct: FoodProduct) {
        val newIngredient = Ingredient(
            id = UUID.randomUUID().toString(),
            recipeId = _createdRecipeDraft.value.id,
            foodProduct = foodProduct,
            quantity = foodProduct.servings.toDouble()
        )
        _createdRecipeDraft.update { it.copy(addedIngredient = it.addedIngredient + newIngredient) }
    }

    override fun onIngredientRemovedInSummary(foodProduct: FoodProduct) {
        _createdRecipeDraft.update { draft ->
            draft.copy(
                addedIngredient = draft.addedIngredient.filterNot { it.foodProduct.id == foodProduct.id }
            )
        }
    }

    override fun onIngredientRemovedInCreation(foodProduct: FoodProduct) {
        _createdRecipeDraft.update { draft ->
            draft.copy(
                ingredient = draft.ingredient.filterNot { it.foodProduct.id == foodProduct.id },
                viewIngredients = draft.viewIngredients.filterNot { it.id == foodProduct.id }
            )
        }
    }

    override suspend fun onSaveAddedIngredients() =
        _createdRecipeDraft.update { draft ->
            draft.copy(
                ingredient = draft.ingredient + draft.addedIngredient,
                addedIngredient = emptyList(),
                viewIngredients = draft.viewIngredients
                        + draft.addedIngredient.map { it.foodProduct }
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
        createdRecipe.ingredient.let {
            if (it.isEmpty()) {
                _errorState.value = R.string.create_recipe_error_ingredients
                return
            }
        }
        _errorState.value = null
        val nutritionalValues = calculateNutritionalValues(createdRecipe.ingredient)
        val recipe = Recipe(
            id = createdRecipe.id,
            name = createdRecipe.title,
            calories = nutritionalValues["calories"]!!,
            carbohydrates = nutritionalValues["carbohydrates"]!!,
            protein = nutritionalValues["protein"]!!,
            fat = nutritionalValues["fat"]!!,
            servings = 1,
            ingredients = createdRecipe.ingredient,
            instructions = createdRecipe.description,
            visibility = RecipeVisibility.OWNER
        )
        viewModelScope.launch {
            recipeRepository.insertRecipe(recipe)
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