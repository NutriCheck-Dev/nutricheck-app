package com.frontend.nutricheck.client.ui.view_model.recipe.report

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    data object ReportClicked : ReportRecipeEvent
    data object DismissDialog : ReportRecipeEvent
    data object SendReport : ReportRecipeEvent
}

@HiltViewModel
class ReportRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
) : BaseReportRecipeViewModel() {
    private val _reportRecipeState = MutableStateFlow(ReportRecipeState())
    val reportRecipeState = _reportRecipeState.asStateFlow()
    private val _events = MutableSharedFlow<ReportRecipeEvent>()
    val events: SharedFlow<ReportRecipeEvent> = _events.asSharedFlow()

    fun onEvent(event: ReportRecipeEvent) {
        when (event) {
            is ReportRecipeEvent.InputTextChanged -> onInputTextChanged(event.inputText)
            is ReportRecipeEvent.ReportClicked -> onReportClick()
            is ReportRecipeEvent.DismissDialog -> onDismissDialog()
            is ReportRecipeEvent.SendReport -> viewModelScope.launch { onClickSendReport() }
        }
    }

    private fun emitEvent(event: ReportRecipeEvent) = viewModelScope.launch { _events.emit(event) }
    override fun onInputTextChanged(text: String) =
        _reportRecipeState.update { it.copy(inputText = text) }

    override fun onReportClick() =
        _reportRecipeState.update { it.copy(reporting = true) }

    override fun onDismissDialog() =
        _reportRecipeState.update { it.copy(reporting = false, inputText = "") }

    override suspend fun onClickSendReport() {
        val recipeReport = RecipeReport(
            recipeId = _reportRecipeState.value.recipe!!.id,
            recipeName = _reportRecipeState.value.recipe!!.name,
            recipeInstructions = _reportRecipeState.value.recipe!!.instructions,
            description = _reportRecipeState.value.inputText
        )
        //TODO: Implement the actual report sending logic
        recipeRepository.updateRecipe(_reportRecipeState.value.recipe!!)
        _reportRecipeState.update { it.copy(reporting = false, inputText = "") }
        emitEvent(ReportRecipeEvent.SendReport)
    }
}