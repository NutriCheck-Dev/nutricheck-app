package com.frontend.nutricheck.client.ui.view_model.recipe

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.utils.CombinedSearchListStore
import com.frontend.nutricheck.client.ui.view_model.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecipeOverviewMode {
    abstract val recipeId: String
    data class General(override val recipeId: String) : RecipeOverviewMode()
    data class FromMeal(override val recipeId: String, val mealId: String) : RecipeOverviewMode()
    data class FromSearch(override val recipeId: String, val fromSearch: Boolean): RecipeOverviewMode()
}

data class CommonRecipeOverviewParams(
    val ingredients: List<Ingredient> = emptyList(),
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fat: Double = 0.0,
    val servings: Double = 1.0,
    val showDetails: Boolean = false,
    val showReportDialog: Boolean = false
)

data class RecipeOverviewState (
    val mode: RecipeOverviewMode,
    val recipe: Recipe,
    val mealId: String? = null,
    val parameters: CommonRecipeOverviewParams
) {
    fun submitRecipe(): Recipe {
        return recipe.copy(
            servings = parameters.servings
        )
    }
}
sealed interface RecipeOverviewEvent {
    data class ClickDetailsOption(val option: DropdownMenuOptions) : RecipeOverviewEvent
    data class ServingsChanged(val servings: Double) : RecipeOverviewEvent
    data class NavigateToEditRecipe(val recipeId: String) : RecipeOverviewEvent
    data object ClickDetails : RecipeOverviewEvent
    data object RecipeUploaded : RecipeOverviewEvent
    data object ResetErrorState : RecipeOverviewEvent
    data object UpdateMealRecipeItem : RecipeOverviewEvent
    data object RecipeDeleted : RecipeOverviewEvent
}

/**
 * ViewModel for managing the recipe overview screen.
 * Handles different modes of recipe display, including general, from meal, and from search.
 * Provides functionality for updating recipe parameters, handling user interactions
 * and managing recipe-related events such as deletion, upload, and editing.
 *
 * @property recipeRepository Repository for managing recipe data.
 * @property historyRepository Repository for managing meal history data.
 * @property snackbarManager Manager for displaying snackbars.
 * @property combinedSearchListStore Store for managing combined search list state.
 * @property context Application context for accessing resources and services.
 * @param savedStateHandle Saved state handle for managing state across configuration changes.
 */
@HiltViewModel
class RecipeOverviewViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val historyRepository: HistoryRepository,
    private val snackbarManager: SnackbarManager,
    private val combinedSearchListStore: CombinedSearchListStore,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
): BaseViewModel() {
    private val mode: RecipeOverviewMode = savedStateHandle.run {
        val recipeId = savedStateHandle.get<String>("recipeId")?.takeIf { it.isNotBlank() }
        val mealId = savedStateHandle.get<String>("mealId")?.takeIf { it.isNotBlank() }
        val fromSearch = savedStateHandle.get<Boolean>("fromSearch") ?: false
        when {
            recipeId != null && mealId != null -> RecipeOverviewMode.FromMeal(recipeId, mealId)
            recipeId != null && fromSearch -> RecipeOverviewMode.FromSearch(recipeId, true)
            recipeId != null -> RecipeOverviewMode.General(recipeId)
            else -> throw IllegalArgumentException("Recipe ID and visibility must be provided")
        }
    }

    private val initialParams = CommonRecipeOverviewParams()
    private val initialRecipe = Recipe()
    private val initialState = RecipeOverviewState(
        mode = mode,
        recipe = initialRecipe,
        mealId = (mode as? RecipeOverviewMode.FromMeal)?.mealId,
        parameters = initialParams
    )

    private var _recipeOverviewState = MutableStateFlow(initialState)
    val recipeOverviewState: StateFlow<RecipeOverviewState> = _recipeOverviewState.asStateFlow()

    init {
        viewModelScope.launch {
            when(mode) {
                is RecipeOverviewMode.FromSearch -> {
                    val searchList = combinedSearchListStore.state.first()
                    val foodComponent = searchList.find { it.id == mode.recipeId } ?: recipeRepository.getRecipeById(mode.recipeId)
                    val recipe = foodComponent as Recipe
                    _recipeOverviewState.update { state ->
                        val params = state.parameters.copy(
                            ingredients = recipe.ingredients,
                            calories = recipe.calories,
                            protein = recipe.protein,
                            carbohydrates = recipe.carbohydrates,
                            fat = recipe.fat,
                            servings = recipe.servings
                        )
                        state.copy(recipe = recipe, parameters = params)
                    }
                }
                is RecipeOverviewMode.General ->
                    recipeRepository.observeRecipeById(mode.recipeId)
                        .collect { recipe ->
                            _recipeOverviewState.update { state ->
                                val params = state.parameters.copy(
                                    ingredients = recipe.ingredients,
                                    calories = recipe.calories,
                                    protein = recipe.protein,
                                    carbohydrates = recipe.carbohydrates,
                                    fat = recipe.fat,
                                    servings = recipe.servings
                                )
                                state.copy(recipe = recipe, parameters = params)
                            }
                        }
                is RecipeOverviewMode.FromMeal -> {
                    val meal = historyRepository.getMealRecipeItemById(mode.mealId, mode.recipeId)
                    val recipe = meal.recipe
                    _recipeOverviewState.update { state ->
                        val params = state.parameters.copy(
                            ingredients = recipe.ingredients,
                            calories = recipe.calories,
                            protein = recipe.protein,
                            carbohydrates = recipe.carbohydrates,
                            fat = recipe.fat,
                            servings = meal.quantity
                        )
                        state.copy(recipe = recipe, parameters = params)
                    }
                }
            }
        }
    }


    private val _events = MutableSharedFlow<RecipeOverviewEvent>()
    val events: SharedFlow<RecipeOverviewEvent> = _events.asSharedFlow()



    fun onEvent(event: RecipeOverviewEvent) {
        when (event) {
            is RecipeOverviewEvent.ClickDetailsOption -> onDetailsOptionClick(event.option)
            is RecipeOverviewEvent.ClickDetails -> onDetailsClicked()
            is RecipeOverviewEvent.ServingsChanged -> onServingsChanged(event.servings)
            is RecipeOverviewEvent.NavigateToEditRecipe -> viewModelScope.launch {
                _events.emit(RecipeOverviewEvent.NavigateToEditRecipe(event.recipeId))
            }
            is RecipeOverviewEvent.ResetErrorState -> setReady()
            is RecipeOverviewEvent.UpdateMealRecipeItem -> viewModelScope.launch { updateMealRecipeItem() }
            else -> { /* Other Events are not handled here */ }
        }
    }

    private fun onServingsChanged(servings: Double) {
        val actualServings = _recipeOverviewState.value.recipe.servings
        val newServings = servings / actualServings
        _recipeOverviewState.update { state ->
            state.copy(
                parameters = state.parameters.copy(servings = servings)
            )
        }
        convertNutrients(newServings)
    }

    private fun onDetailsClicked() {
        _recipeOverviewState.update { state ->
            state.copy(
                parameters = state.parameters.copy(showDetails = !state.parameters.showDetails)
            )
        }
    }

    private fun convertNutrients(servings: Double) {
        _recipeOverviewState.update { state ->
            val initialRecipe = state.recipe
            state.copy(parameters = state.parameters.copy(
                calories = servings * initialRecipe.calories,
                protein = servings * initialRecipe.protein,
                carbohydrates = servings * initialRecipe.carbohydrates,
                fat = servings * initialRecipe.fat)
            )
        }
    }

    private suspend fun updateMealRecipeItem() {
        if (mode is RecipeOverviewMode.FromMeal) {
            val mealRecipeItem = MealRecipeItem(
                mealId = mode.mealId,
                recipe = _recipeOverviewState.value.recipe,
                servings = _recipeOverviewState.value.parameters.servings,
                quantity = _recipeOverviewState.value.parameters.servings,
            )
            historyRepository.updateMealRecipeItem(mealRecipeItem)
        }
    }

    private fun onDetailsOptionClick(option: DropdownMenuOptions) {
        viewModelScope.launch {
            when (option) {
                DropdownMenuOptions.DELETE -> {
                    recipeRepository.deleteRecipe(_recipeOverviewState.value.recipe)
                    _events.emit(RecipeOverviewEvent.RecipeDeleted)
                    snackbarManager.show(context.getString(R.string.snackbar_message_recipe_deleted))

                }
                DropdownMenuOptions.DOWNLOAD -> {
                    recipeRepository.downloadRecipe(_recipeOverviewState.value.recipe)
                    snackbarManager.show(context.getString(R.string.snackbar_message_recipe_downloaded))

                }
                DropdownMenuOptions.UPLOAD -> {
                    when (val body = recipeRepository.uploadRecipe(_recipeOverviewState.value.recipe)) {
                        is Result.Success -> {
                            _events.emit(RecipeOverviewEvent.RecipeUploaded)
                            snackbarManager.show(context.getString(R.string.snackbar_message_recipe_uploaded))
                        }
                        is Result.Error -> setError(body.message!!)
                    }
                }
                DropdownMenuOptions.REPORT -> _recipeOverviewState.update { it.copy(parameters = it.parameters.copy(showReportDialog = !it.parameters.showReportDialog)) }
                DropdownMenuOptions.EDIT -> _events.emit(RecipeOverviewEvent.NavigateToEditRecipe(_recipeOverviewState.value.recipe.id))
            }
        }
    }
}