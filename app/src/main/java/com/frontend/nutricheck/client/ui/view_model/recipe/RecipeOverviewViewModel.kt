package com.frontend.nutricheck.client.ui.view_model.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_component.CombinedSearchListStore
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
    val servings: Int = 1,
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
        return Recipe(
            id = recipe.id,
            name = recipe.name,
            ingredients = parameters.ingredients,
            calories = parameters.calories,
            protein = parameters.protein,
            carbohydrates = parameters.carbohydrates,
            fat = parameters.fat,
            servings = parameters.servings
        )
    }
}
sealed interface RecipeOverviewEvent {
    data class ClickDetailsOption(val option: DropdownMenuOptions) : RecipeOverviewEvent
    data class ServingsChanged(val servings: Int) : RecipeOverviewEvent
    data object ClickDetails : RecipeOverviewEvent
}

@HiltViewModel
class RecipeOverviewViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val historyRepository: HistoryRepository,
    private val combinedSearchListStore: CombinedSearchListStore,
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
            val recipe = when(mode) {
                is RecipeOverviewMode.FromSearch -> {
                    val searchList = combinedSearchListStore.state.first()
                    searchList.find { it.id == mode.recipeId } ?: recipeRepository.getRecipeById(mode.recipeId)
                }
                is RecipeOverviewMode.General -> recipeRepository.getRecipeById(mode.recipeId)
                is RecipeOverviewMode.FromMeal -> {
                    val meal = historyRepository.getMealRecipeItemById(mode.mealId, mode.recipeId)
                    meal.recipe
                }
            }
            val newParams = initialParams.copy(
                ingredients = (recipe as Recipe).ingredients,
                calories = recipe.calories,
                protein = recipe.protein,
                carbohydrates = recipe.carbohydrates,
                fat = recipe.fat,
                servings = recipe.servings
                )

            _recipeOverviewState.update { it.copy(recipe = recipe, parameters = newParams) }
        }
    }


    private val _events = MutableSharedFlow<RecipeOverviewEvent>()
    val events: SharedFlow<RecipeOverviewEvent> = _events.asSharedFlow()



    fun onEvent(event: RecipeOverviewEvent) {
        when (event) {
            is RecipeOverviewEvent.ClickDetailsOption -> onDetailsOptionClick(event.option)
            is RecipeOverviewEvent.ClickDetails -> onDetailsClicked()
            is RecipeOverviewEvent.ServingsChanged -> onServingsChanged(event.servings)
        }
    }

    private fun onServingsChanged(servings: Int) {
        _recipeOverviewState.update { state ->
            state.copy(
                parameters = state.parameters.copy(servings = servings)
            )
        }
        convertNutrients()
    }

    private fun onDetailsClicked() {
        _recipeOverviewState.update { state ->
            state.copy(
                parameters = state.parameters.copy(showDetails = !state.parameters.showDetails)
            )
        }
    }

    private fun convertNutrients() {
        _recipeOverviewState.update { state ->
            val parameters = state.parameters
            val initialRecipe = state.recipe
            val servings = parameters.servings
            state.copy(parameters = state.parameters.copy(
                calories = servings * initialRecipe.calories,
                protein = servings * initialRecipe.protein,
                carbohydrates = servings * initialRecipe.carbohydrates,
                fat = servings * initialRecipe.fat)
            )
        }
    }

    private fun onDetailsOptionClick(option: DropdownMenuOptions) {
        viewModelScope.launch {
            when (option) {
                DropdownMenuOptions.DELETE -> recipeRepository.deleteRecipe(_recipeOverviewState.value.recipe)
                DropdownMenuOptions.DOWNLOAD -> recipeRepository.insertRecipe(_recipeOverviewState.value.recipe)
                DropdownMenuOptions.UPLOAD -> recipeRepository.uploadRecipe(_recipeOverviewState.value.recipe)
                DropdownMenuOptions.REPORT -> _recipeOverviewState.update { it.copy(parameters = it.parameters.copy(showReportDialog = !it.parameters.showReportDialog)) }
                DropdownMenuOptions.EDIT -> {}
            }
        }
    }
}