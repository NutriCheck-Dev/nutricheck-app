package com.frontend.nutricheck.client.ui.view_model.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
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
    val ingredients: List<Pair<Double, FoodComponent>> = emptyList(),
    val expanded: Boolean = false,
    val language: String = "",
    val query: String = "",
    val results: List<FoodComponent> = emptyList(),
    val confirmationDialog : Boolean = false
)

sealed interface RecipeEditorEvent {
    data class TitleChanged(val title: String) : RecipeEditorEvent
    data class DescriptionChanged(val description: String) : RecipeEditorEvent
    data class IngredientAdded(val foodProduct: Pair<Double, FoodComponent>) : RecipeEditorEvent
    data class IngredientRemoved(val foodProduct: FoodComponent) : RecipeEditorEvent
    data class QueryChanged(val query: String) : RecipeEditorEvent
    object ShowConfirmationDialog : RecipeEditorEvent
    object SearchIngredients : RecipeEditorEvent
    object SaveRecipe : RecipeEditorEvent
    object Cancel : RecipeEditorEvent
    object ExpandBottomSheet : RecipeEditorEvent
}

@HiltViewModel
class RecipeEditorViewModel @Inject constructor(
    private val recipeRepo: RecipeRepository,
    private val appSettingRepository: AppSettingRepository,
    private val foodProductRepository: FoodProductRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val mode: RecipeMode =
        savedStateHandle.get<String>("recipeId")
            ?.takeIf { it.isNotEmpty() }
            ?.let { RecipeMode.Edit(it) }
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
                    ingredients = recipe.ingredients.map { ingredient ->
                        Pair(ingredient.quantity, ingredient.foodProduct)
                    }
                )
                appSettingRepository.language.collect { language ->
                    _draft.update { draft ->
                        draft.copy(language = language.code)
                    }
                }
            }
        }
    }

    private val _events = MutableSharedFlow<RecipeEditorEvent>()
    val events: SharedFlow<RecipeEditorEvent> = _events.asSharedFlow()

    fun onEvent(event: RecipeEditorEvent) {
        when (event) {
            is RecipeEditorEvent.TitleChanged -> titleChanged(event.title)
            is RecipeEditorEvent.DescriptionChanged -> descriptionChanged(event.description)
            is RecipeEditorEvent.IngredientAdded -> addIngredients(event.foodProduct)
            is RecipeEditorEvent.IngredientRemoved -> removeIngredient(event.foodProduct)
            is RecipeEditorEvent.SaveRecipe -> saveRecipe()
            is RecipeEditorEvent.Cancel -> cancel()
            is RecipeEditorEvent.ExpandBottomSheet -> {
                _draft.update { draft ->
                    draft.copy(expanded = !draft.expanded)
                }
            }
            is RecipeEditorEvent.SearchIngredients -> onSearchIngredients()
            is RecipeEditorEvent.QueryChanged -> onQueryChanged(event.query)
            is RecipeEditorEvent.ShowConfirmationDialog -> changeShowConfirmationDialog()
        }
    }

    private fun titleChanged(title: String) {
        setReady()
        _draft.value = _draft.value.copy(title = title)
    }

    private fun descriptionChanged(description: String) {
        _draft.value = _draft.value.copy(description = description)
    }

    private fun addIngredients(foodComponent: Pair<Double, FoodComponent>) {
        _draft.update { draft ->
            val current = draft.ingredients
            val existing = current.find { it.second.id == foodComponent.second.id }
            val newAddedComponents = if (existing != null) {
                current.filterNot { it.second.id == foodComponent.second.id } +
                        Pair(existing.first + foodComponent.first, existing.second)
            } else {
                current + foodComponent
            }
            draft.copy(ingredients = newAddedComponents)
        }
    }

    private fun removeIngredient(foodProduct: FoodComponent) =
        _draft.update { draft ->
            val currentIngredients = draft.ingredients
            val newIngredients = currentIngredients.filterNot { it.second.id == foodProduct.id }
            draft.copy(ingredients = newIngredients)
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
            acc["calories"] = acc["calories"]!! + ingredient.second.calories * ingredient.first
            acc["carbohydrates"] = acc["carbohydrates"]!! + ingredient.second.carbohydrates * ingredient.first
            acc["protein"] = acc["protein"]!! + ingredient.second.protein * ingredient.first
            acc["fat"] = acc["fat"]!! + ingredient.second.fat * ingredient.first
            acc
        }
        val actualIngredients = draft.ingredients.map {
            Ingredient(
                recipeId = draft.id,
                foodProduct = it.second as FoodProduct,
                quantity = it.first
            )
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
            ingredients = actualIngredients,
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
                ingredients = original.ingredients.map { Pair(it.quantity, it.foodProduct) }
            )
        }
    }

    private fun onSearchIngredients() {
        val query = _draft.value.query
        if (query.isBlank()) {
            setError("Please enter a search term.")
            return
        }

        viewModelScope.launch {
            val language = _draft.value.language
            val foodProducts = foodProductRepository
                .searchFoodProducts(query, language)
            foodProducts
                .onStart { setLoading() }
                .catch { setError(it.message!!) }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _draft.update { draft ->
                                draft.copy(
                                        results = result.data
                                    )
                            }
                        }
                        is Result.Error -> {
                            setError(result.message!!)
                        }
                    }
                }
            setReady()
        }
    }

    private fun changeShowConfirmationDialog() =
        _draft.update { draft ->
            draft.copy(confirmationDialog = !_draft.value.confirmationDialog)
        }

    private fun onQueryChanged(query: String) =
        _draft.update { draft ->
            draft.copy(query = query)
        }

    private fun emitEvent(event: RecipeEditorEvent) =
        viewModelScope.launch { _events.emit(event) }
}