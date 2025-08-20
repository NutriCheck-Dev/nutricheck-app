package com.frontend.nutricheck.client.ui.view_model.recipe

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportRecipeState(
    val recipe: Recipe? = null,
    val inputText: String = "",
    val reporting: Boolean = false
)

sealed interface ReportRecipeEvent {
    data class InputTextChanged(val inputText: String) : ReportRecipeEvent
    data class ReportClicked(val recipe: Recipe) : ReportRecipeEvent
    data object DismissDialog : ReportRecipeEvent
    data object SendReport : ReportRecipeEvent
}

@HiltViewModel
class ReportRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val snackbarManager: SnackbarManager,
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    private val _reportRecipeState = MutableStateFlow(ReportRecipeState())
    val reportRecipeState = _reportRecipeState.asStateFlow()
    private val _events = MutableSharedFlow<ReportRecipeEvent>()
    val events: SharedFlow<ReportRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: ReportRecipeEvent) {
        when (event) {
            is ReportRecipeEvent.InputTextChanged -> onInputTextChanged(event.inputText)
            is ReportRecipeEvent.ReportClicked -> onReportClick(event.recipe)
            is ReportRecipeEvent.DismissDialog -> onDismissDialog()
            is ReportRecipeEvent.SendReport -> viewModelScope.launch { onClickSendReport() }
        }
    }

    private fun onInputTextChanged(text: String) =
        _reportRecipeState.update { it.copy(inputText = text) }

    private fun onReportClick(recipe: Recipe) {
        _reportRecipeState.update { it.copy(recipe = recipe) }
        _reportRecipeState.update { it.copy(reporting = true) }
    }

    private fun onDismissDialog() {
        _reportRecipeState.update { it.copy(recipe = null, inputText = "") }
        _reportRecipeState.update { it.copy(reporting = false) }
    }


    private suspend fun onClickSendReport() {
        val recipeReport = RecipeReport(
            recipeId = _reportRecipeState.value.recipe!!.id,
            recipeName = _reportRecipeState.value.recipe!!.name,
            recipeInstructions = _reportRecipeState.value.recipe!!.instructions,
            description = _reportRecipeState.value.inputText
        )
        recipeRepository.reportRecipe(recipeReport)
        _reportRecipeState.update { it.copy(recipe = null, reporting = false, inputText = "") }
        snackbarManager.show(context.getString(R.string.snackbar_message_recipe_reported))
    }
}