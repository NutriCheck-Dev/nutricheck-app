package com.frontend.nutricheck.client.ui.view_model.recipe.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class RecipeMode {
    object Create : RecipeMode()
    data class Edit(val recipeId: String) : RecipeMode()
}

data class RecipeDraft(
    val original: Recipe? = null,
    val id: String,
    val title: String,
    val description: String,
    val servings: Int,
    val ingredients: List<Ingredient> = emptyList()
) {
    fun toRecipe(): Recipe {
        return Recipe(
            id = id,
            name = title,
            instructions = description,
            servings = servings,
            ingredients = ingredients,
            calories = ingredients.sumOf { it.foodProduct.calories * it.quantity },
            carbohydrates = ingredients.sumOf { it.foodProduct.carbohydrates * it.quantity },
            protein = ingredients.sumOf { it.foodProduct.protein * it.quantity },
            fat = ingredients.sumOf { it.foodProduct.fat * it.quantity },
            visibility = original?.visibility ?: RecipeVisibility.OWNER
        )
    }
}

sealed interface RecipeEditorEvent {
    data class TitleChanged(val title: String) : RecipeEditorEvent
    data class DescriptionChanged(val description: String) : RecipeEditorEvent
    data class IngredientAdded(val ingredients: List<Ingredient>) : RecipeEditorEvent
    data class IngredientRemoved(val ingredient: Ingredient) : RecipeEditorEvent
    object SaveRecipe : RecipeEditorEvent
    object Cancel : RecipeEditorEvent
}

@HiltViewModel
class RecipeEditorViewModel @Inject constructor(
    private val recipeRepo: RecipeRepositoryImpl,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val mode: RecipeMode =
        savedStateHandle.get<String>("recipeId")?.let { RecipeMode.Edit(it) }
            ?: RecipeMode.Create

    private val _draft = MutableStateFlow(
        RecipeDraft(
            original = null,
            id = UUID.randomUUID().toString(),
            title = "",
            description = "",
            servings = 1
        )
    )
    val draft = _draft.asStateFlow()

    init {
        if (mode is RecipeMode.Edit) {
            viewModelScope.launch {
                val recipe = recipeRepo.getRecipeById(mode.recipeId)
                _draft.value = RecipeDraft(
                    original = recipe,
                    id = recipe.id,
                    title = recipe.name,
                    description = recipe.instructions,
                    servings = recipe.servings,
                    ingredients = recipe.ingredients
                )
            }
        }
    }

    private val _events = MutableSharedFlow<RecipeEditorEvent>()
    val events: SharedFlow<RecipeEditorEvent> = _events.asSharedFlow()

    fun onEvent(event: RecipeEditorEvent) {
        when (event) {
            is RecipeEditorEvent.TitleChanged -> titleChanged(event.title)
            is RecipeEditorEvent.DescriptionChanged -> descriptionChanged(event.description)
            is RecipeEditorEvent.IngredientAdded -> addIngredients(event.ingredients)
            is RecipeEditorEvent.IngredientRemoved -> removeIngredient(event.ingredient)
            is RecipeEditorEvent.SaveRecipe -> saveRecipe()
            is RecipeEditorEvent.Cancel -> cancel()
        }
    }

    private fun titleChanged(title: String) {
        if (title.isBlank()) {
            setError("Bitte geben sie ihrem Rezept einen Namen.")
        } else {
            setReady()
            _draft.value = _draft.value.copy(title = title)
        }
    }

    private fun descriptionChanged(description: String) {
        _draft.value = _draft.value.copy(description = description)
    }

    private fun addIngredients(ingredients: List<Ingredient>) {
        _draft.update { draft ->
            val current = draft.ingredients.toMutableList()
            for (newIngredient in ingredients) {
                val index = current.indexOfFirst { it.foodProduct.id == newIngredient.foodProduct.id }
                if (index >= 0) {
                    val existing = current[index]
                    val combined = existing.copy(quantity = existing.quantity + newIngredient.quantity)
                    current[index] = combined
                } else {
                    current.add(newIngredient)
                }
            }
            draft.copy(ingredients = current)
        }
    }

    private fun removeIngredient(ingredient: Ingredient) {
        _draft.update { it.copy(
            ingredients = it.ingredients.filterNot { ingredient -> ingredient.foodProduct.id == ingredient.foodProduct.id }
        ) }
    }

    private fun saveRecipe() {
        val draft = _draft.value
        if (draft.title.isBlank()) {
            setError("Bitte geben sie ihrem Rezept einen Namen.")
            return
        }
        if (draft.ingredients.isEmpty()) {
            setError("Bitte fügen sie mindestens eine Zutat hinzu.")
            return
        }
        setReady()
        val totals = draft.ingredients.fold(
            mutableMapOf(
                "calories" to 0.0,
                "carbohydrates" to 0.0,
                "protein" to 0.0,
                "fat" to 0.0
            )
        ) { acc, ingredient ->
            acc["calories"] = acc["calories"]!! + ingredient.foodProduct.calories * ingredient.quantity
            acc["carbohydrates"] = acc["carbohydrates"]!! + ingredient.foodProduct.carbohydrates * ingredient.quantity
            acc["protein"] = acc["protein"]!! + ingredient.foodProduct.protein * ingredient.quantity
            acc["fat"] = acc["fat"]!! + ingredient.foodProduct.fat * ingredient.quantity
            acc
        }
        val recipe = Recipe(
            id = draft.id,
            name = draft.title,
            calories = totals["calories"]!!,
            carbohydrates = totals["carbohydrates"]!!,
            protein = totals["protein"]!!,
            fat = totals["fat"]!!,
            servings = draft.servings,
            instructions = draft.description,
            ingredients = draft.ingredients,
            visibility = draft.original?.visibility ?: RecipeVisibility.OWNER
        )

        viewModelScope.launch {
            when (mode) {
                is RecipeMode.Create -> recipeRepo.insertRecipe(recipe)
                is RecipeMode.Edit -> recipeRepo.uploadRecipe(recipe)
            }
        }
    }

    private fun cancel() {
        setReady()
        _draft.value = when (val original = _draft.value.original) {
            null -> RecipeDraft(
                id = UUID.randomUUID().toString(),
                title = "",
                description = "",
                servings = 1
            )
            else -> RecipeDraft(
                original = original,
                id = original.id,
                title = original.name,
                description = original.instructions,
                servings = original.servings,
                ingredients = original.ingredients
            )
        }
    }

    private fun emitEvent(event: RecipeEditorEvent) =
        viewModelScope.launch { _events.emit(event) }
}