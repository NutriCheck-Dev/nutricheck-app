package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.RecipeOverviewBaseContent
import com.frontend.nutricheck.client.ui.view.widgets.RecipeOverviewEditContent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverview(
    modifier: Modifier = Modifier,
    //actions: NavigationActions,
    recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(),
    editRecipeViewModel: EditRecipeViewModel = hiltViewModel(),
    reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val recipeOverviewState by recipeOverviewViewModel.recipeOverviewState.collectAsState()
    val draftState by editRecipeViewModel.editRecipeDraft.collectAsState()
    val isEditing = recipeOverviewState.isEditing

    if (!isEditing) {
        RecipeOverviewBaseContent(
            recipe = recipeOverviewState.recipe,
            onDownLoad = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickDownloadRecipe(it)) },
            onEdit = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickEditRecipe) },
            onDelete = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickDeleteRecipe(it)) },
            onUpload = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickUploadRecipe(it)) },
            onReport = { reportRecipeViewModel.onEvent(ReportRecipeEvent.SendReport) }, //TODO: Implement report functionality
            onDismiss = {}, //TODO: Implement dismiss functionality
            onReportClick = {}, //TODO: Implement report click functionality
            onBack = onBack
        )
    } else {
        draftState?.let { draft ->
            RecipeOverviewEditContent(
                draft = draft,
                onEvent = editRecipeViewModel::onEvent,
                onSave = { editRecipeViewModel.onEvent(EditRecipeEvent.RecipeSaved) },
                onCancel = { editRecipeViewModel.onEvent(EditRecipeEvent.EditCanceled) }
            )
        }
    }

    LaunchedEffect(editRecipeViewModel.events) {
        editRecipeViewModel.events.collect { event ->
            when (event) {
                is EditRecipeEvent.EditCanceled,
                is EditRecipeEvent.RecipeSaved -> {
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
    AppTheme(darkTheme = true) {
        RecipeOverview()
    }
}

