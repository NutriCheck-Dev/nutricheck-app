package com.frontend.nutricheck.client.ui.view_model.recipe

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
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
import com.frontend.nutricheck.client.ui.view_model.utils.CombinedSearchListStore
import com.frontend.nutricheck.client.ui.view_model.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val ingredients: List<FoodComponent> = emptyList(),
    val expanded: Boolean = false,
    val language: String = "",
    val query: String = "",
    val results: List<FoodComponent> = emptyList(),
    val confirmationDialog : Boolean = false,
    val hasSearched: Boolean = false,
    val lastSearchedQuery: String? = null
)

sealed interface RecipeEditorEvent {
    data class TitleChanged(val title: String) : RecipeEditorEvent
    data class DescriptionChanged(val description: String) : RecipeEditorEvent
    data class ServingsChanged(val servings: Int) : RecipeEditorEvent
    data class IngredientAdded(val foodProduct: FoodComponent) : RecipeEditorEvent
    data class IngredientRemoved(val foodProduct: FoodComponent) : RecipeEditorEvent
    data class QueryChanged(val query: String) : RecipeEditorEvent
    object ShowConfirmationDialog : RecipeEditorEvent
    object SearchIngredients : RecipeEditorEvent
    object SaveRecipe : RecipeEditorEvent
    object RecipeSaved : RecipeEditorEvent
    object ExpandBottomSheet : RecipeEditorEvent
    object ResetErrorState : RecipeEditorEvent
}

/**
 * ViewModel for managing the recipe editor state and actions.
 * Handles creating and editing recipes, managing ingredients
 * and interacting with repositories for data operations.
 *
 * @property recipeRepo Repository for managing recipes.
 * @property appSettingRepository Repository for managing app settings.
 * @property foodProductRepository Repository for managing food products.
 * @property snackbarManager Manager for displaying snackbars.
 * @property combinedSearchListStore Store for managing the combined search list of ingredients.
 * @property context Application context for accessing resources.
 * @property savedStateHandle Saved state handle for managing state across configuration changes.
 */
@HiltViewModel
class RecipeEditorViewModel @Inject constructor(
    private val recipeRepo: RecipeRepository,
    private val appSettingRepository: AppSettingRepository,
    private val foodProductRepository: FoodProductRepository,
    private val snackbarManager: SnackbarManager,
    private val combinedSearchListStore: CombinedSearchListStore,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val mode: RecipeMode =
        savedStateHandle.get<String>("recipeId")
            ?.takeIf { it.isNotBlank() }
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
                        ingredient.foodProduct.copy(
                            servings = ingredient.servings,
                            servingSize = ingredient.servingSize
                        )
                    }
                )
                combinedSearchListStore.update(
                    _draft.value.ingredients + _draft.value.results
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
            is RecipeEditorEvent.ServingsChanged -> onServingsChanged(event.servings)
            is RecipeEditorEvent.IngredientAdded -> addIngredients(event.foodProduct)
            is RecipeEditorEvent.IngredientRemoved -> removeIngredient(event.foodProduct)
            is RecipeEditorEvent.SaveRecipe -> saveRecipe()
            is RecipeEditorEvent.ExpandBottomSheet -> {
                _draft.update { draft ->
                    draft.copy(expanded = !draft.expanded)
                }
            }
            is RecipeEditorEvent.SearchIngredients -> onSearchIngredients()
            is RecipeEditorEvent.QueryChanged -> onQueryChanged(event.query)
            is RecipeEditorEvent.ShowConfirmationDialog -> changeShowConfirmationDialog()
            is RecipeEditorEvent.ResetErrorState -> setReady()
            is RecipeEditorEvent.RecipeSaved -> null
        }
    }

    private fun titleChanged(title: String) {
        setReady()
        _draft.value = _draft.value.copy(title = title)
    }

    private fun descriptionChanged(description: String) {
        _draft.value = _draft.value.copy(description = description)
    }

    private fun addIngredients(foodComponent: FoodComponent) {
        viewModelScope.launch{
            _draft.update { draft ->
                val current = draft.ingredients
                val existing = current.find { it.id == foodComponent.id }
                val newAddedComponents = if (existing != null) {
                    current.filterNot { it.id == foodComponent.id } +
                            foodComponent
                } else {
                    current + foodComponent
                }
                val combinedList = newAddedComponents + draft.results.filter { it.id != foodComponent.id }
                combinedSearchListStore.update(combinedList)
                draft.copy(
                    ingredients = newAddedComponents,
                    results = _draft.value.results.filterNot { it.id == foodComponent.id }
                )
            }
            snackbarManager.show(context.getString(R.string.snackbar_message_ingredient_added))
            _events.emit(RecipeEditorEvent.IngredientAdded(foodComponent))
        }
    }

    private fun removeIngredient(foodProduct: FoodComponent) {
        _draft.update { draft ->
            val currentIngredients = draft.ingredients
            val newIngredients = currentIngredients.filterNot { it.id == foodProduct.id }
            val combinedList = newIngredients + draft.results + foodProduct
            combinedSearchListStore.update(combinedList)
            draft.copy(
                results = draft.results + foodProduct,
                ingredients = newIngredients
            )
        }
        snackbarManager.show(context.getString(R.string.snackbar_message_ingredient_removed))
    }


    private fun saveRecipe() {
        viewModelScope.launch {
        val draft = _draft.value
        if (draft.title.isBlank()) {
            setError(context.getString(R.string.error_no_recipe_name))
            return@launch
        }
        if (draft.ingredients.size < 2) {
            setError(context.getString(R.string.error_not_enough_ingredients))
            return@launch
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
            acc["calories"] = acc["calories"]!! + ingredient.calories * ingredient.servings
            acc["carbohydrates"] = acc["carbohydrates"]!! + ingredient.carbohydrates * ingredient.servings
            acc["protein"] = acc["protein"]!! + ingredient.protein * ingredient.servings
            acc["fat"] = acc["fat"]!! + ingredient.fat * ingredient.servings
            acc
        }
        val actualIngredients = draft.ingredients.map { foodComponent ->
            Ingredient(
                recipeId = draft.id,
                foodProduct = foodComponent as FoodProduct
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


            when (mode) {
                is RecipeMode.Create -> {
                    recipeRepo.insertRecipe(recipe)
                    _events.emit(RecipeEditorEvent.RecipeSaved)
                    snackbarManager.show(context.getString(R.string.snackbar_message_recipe_saved))
                }
                is RecipeMode.Edit -> {
                    recipeRepo.updateRecipe(recipe)
                    _events.emit(RecipeEditorEvent.RecipeSaved)
                    snackbarManager.show(context.getString(R.string.snackbar_message_recipe_updated))
                }
            }
        }
    }

    private fun onSearchIngredients() {
        val query = _draft.value.query
        if (query.isBlank()) return

        _draft.update { draft ->
            draft.copy(
                hasSearched = true,
                lastSearchedQuery = query
            )
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
                            setReady()
                        }
                        is Result.Error -> {
                            setError(result.message!!)
                        }
                    }
                }
            val combinedList = _draft.value.ingredients + _draft.value.results
            combinedSearchListStore.update(combinedList)
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

    private fun onServingsChanged(servings: Int) {
        _draft.update { draft ->
            draft.copy(servings = servings)
        }
    }

}