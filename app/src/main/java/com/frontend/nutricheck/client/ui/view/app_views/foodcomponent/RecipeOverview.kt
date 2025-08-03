package com.frontend.nutricheck.client.ui.view.app_views.foodcomponent

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.RecipeOverviewBaseContent
import com.frontend.nutricheck.client.ui.view.widgets.RecipeOverviewEditContent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverview(
    recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(),
    editRecipeViewModel: RecipeEditorViewModel = hiltViewModel(),
    reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(),
    onItemClick: (Ingredient) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val recipeOverviewState by recipeOverviewViewModel.recipeOverviewState.collectAsState()
    val draftState by editRecipeViewModel.draft.collectAsState()
    val reportRecipeState by reportRecipeViewModel.reportRecipeState.collectAsState()
    val isEditing = recipeOverviewState.parameters.editing

    if (!isEditing) {
        RecipeOverviewBaseContent(
            recipe = recipeOverviewState.recipe,
            onItemClick = { ingredient -> onItemClick(ingredient) },
            onDownLoad = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickDownloadRecipe(it)) },
            onEdit = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickEditRecipe) },
            onDelete = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickDeleteRecipe(it)) },
            onUpload = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickUploadRecipe(it)) },
            onSendReport = {
                reportRecipeViewModel.onEvent(ReportRecipeEvent.SendReport)
                       },
            onDismiss = { reportRecipeViewModel.onEvent(ReportRecipeEvent.DismissDialog) },
            onReportClick = { reportRecipeViewModel.onEvent(ReportRecipeEvent.ReportClicked) },
            onBack = onBack,
            showReportDialog = reportRecipeState.reporting,
            ingredients = recipeOverviewState.parameters.ingredients,
            reportRecipeViewModel = reportRecipeViewModel
        )
    } else {
        draftState.let { draft ->
            RecipeOverviewEditContent(
                draft = draft,
                onEvent = editRecipeViewModel::onEvent,
                onSave = { editRecipeViewModel.onEvent(RecipeEditorEvent.SaveRecipe) },
                onCancel = { editRecipeViewModel.onEvent(RecipeEditorEvent.Cancel) },
                ingredients = draft.ingredients.map { Ingredient(recipeId = draft.id, it.second as FoodProduct, it.first) },
                onItemClick = onItemClick
            )
        }
    }

    LaunchedEffect(editRecipeViewModel.events) {
        editRecipeViewModel.events.collect { event ->
            when (event) {
                is RecipeEditorEvent.Cancel,
                is RecipeEditorEvent.SaveRecipe -> {
                recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickEditRecipe)
                }
                else -> Unit
            }
        }
    }
}

@Preview
@Composable
fun RecipeOverviewPreview() {
    AppTheme {
        RecipeOverview()
    }
}

