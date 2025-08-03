package com.frontend.nutricheck.client.ui.view_model.recipe.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
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

sealed class RecipeOverviewMode {
    abstract val recipeId: String
    data class FromRecipePage(override val recipeId: String, val visibility: String) : RecipeOverviewMode()
    data class FromSearch(override val recipeId: String, val visibility: String) : RecipeOverviewMode()
    data class FromMeal(override val recipeId: String, val mealId: String) : RecipeOverviewMode()
}

data class CommonRecipeOverviewParams(
    val ingredients: List<Ingredient> = emptyList(),
    val editing: Boolean = false,
    val showDetails: Boolean = false,
    val showReportDialog: Boolean = false
)

data class RecipeOverviewState (
    val mode: RecipeOverviewMode,
    val recipe: Recipe,
    val mealId: String? = null,
    val parameters: CommonRecipeOverviewParams
)
sealed interface RecipeOverviewEvent {
    data class ClickDetailsOption(val option: DropdownMenuOptions) : RecipeOverviewEvent
    data object ClickEditRecipe : RecipeOverviewEvent
    data object ClickDetails : RecipeOverviewEvent
}

@HiltViewModel
class RecipeOverviewViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : BaseRecipeOverviewViewModel() {
    private val mode: RecipeOverviewMode = savedStateHandle.run {
        val recipeId = get<String>("recipeId")
        val visibility = get<String>("visibility")
        val mealId = get<String>("mealId")

        when {
            recipeId != null && visibility != null -> {
                if (visibility == RecipeVisibility.OWNER.toString()) {
                    RecipeOverviewMode.FromRecipePage(recipeId, visibility)
                } else {
                    RecipeOverviewMode.FromSearch(recipeId, visibility)
                }
            }
            recipeId != null && mealId != null -> {
                RecipeOverviewMode.FromMeal(recipeId, mealId)
            }
            else -> throw IllegalArgumentException("Recipe ID and visibility must be provided") // Temporary solution
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
            val recipe = recipeRepository.getRecipeById(mode.recipeId)
            val newParams = initialParams.copy(ingredients = recipe.ingredients)
            _recipeOverviewState.update { it.copy(recipe = recipe, parameters = newParams) }
        }
    }


    private val _events = MutableSharedFlow<RecipeOverviewEvent>()
    val events: SharedFlow<RecipeOverviewEvent> = _events.asSharedFlow()



    fun onEvent(event: RecipeOverviewEvent) {
        when (event) {
            is RecipeOverviewEvent.ClickDetailsOption -> onDetailsOptionClick(event.option)
            is RecipeOverviewEvent.ClickEditRecipe -> onEditClicked()
            is RecipeOverviewEvent.ClickDetails -> onDetailsClicked()
        }
    }

    override fun onEditClicked() {
        _recipeOverviewState.update { state ->
            state.copy(
                parameters = state.parameters.copy(editing = !state.parameters.editing)
            )
        }
    }

    private fun onDetailsClicked() {
        _recipeOverviewState.update { state ->
            state.copy(
                parameters = state.parameters.copy(showDetails = !state.parameters.showDetails)
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