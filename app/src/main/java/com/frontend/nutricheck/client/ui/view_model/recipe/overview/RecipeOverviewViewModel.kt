package com.frontend.nutricheck.client.ui.view_model.recipe.overview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
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
    val editing: Boolean = false
)

data class RecipeOverviewState (
    val mode: RecipeOverviewMode,
    val recipe: Recipe,
    val mealId: String? = null,
    val parameters: CommonRecipeOverviewParams
)
sealed interface RecipeOverviewEvent {
    data class ClickDownloadRecipe(val recipe: Recipe) : RecipeOverviewEvent
    data class ClickDeleteRecipe(val recipe: Recipe) : RecipeOverviewEvent
    data class ClickUploadRecipe(val recipe: Recipe) : RecipeOverviewEvent
    data object ClickEditRecipe : RecipeOverviewEvent
}

@HiltViewModel
class RecipeOverviewViewModel @Inject constructor(
    private val recipeRepository: RecipeRepositoryImpl,
    private val historyRepository: HistoryRepositoryImpl,
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
            is RecipeOverviewEvent.ClickDownloadRecipe -> {
                viewModelScope.launch {
                    onDownloadRecipe(event.recipe)
                    _events.emit(RecipeOverviewEvent.ClickDownloadRecipe(event.recipe))
                }
            }
            is RecipeOverviewEvent.ClickEditRecipe -> onEditClicked()
            is RecipeOverviewEvent.ClickDeleteRecipe -> {
                viewModelScope.launch {
                    onDeleteRecipe(event.recipe)
                    _events.emit(RecipeOverviewEvent.ClickDeleteRecipe(event.recipe))
                }
            }
            is RecipeOverviewEvent.ClickUploadRecipe -> {
                viewModelScope.launch {
                    onShareRecipe(event.recipe)
                    _events.emit(RecipeOverviewEvent.ClickUploadRecipe(event.recipe))
                }
            }
        }
    }

    override suspend fun onDownloadRecipe(recipe: Recipe) {
        recipeRepository.insertRecipe(recipe)
    }

    override fun onEditClicked() {
        _recipeOverviewState.update { state ->
            state.copy(
                parameters = state.parameters.copy(editing = true)
            )
        }
    }

    override suspend fun onDeleteRecipe(recipe: Recipe) {
        recipeRepository.deleteRecipe(recipe)
    }

    override suspend fun onShareRecipe(recipe: Recipe) {
        recipeRepository.uploadRecipe(recipe)
    }
}