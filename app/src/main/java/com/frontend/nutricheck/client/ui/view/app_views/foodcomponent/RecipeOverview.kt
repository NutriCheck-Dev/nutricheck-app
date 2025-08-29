package com.frontend.nutricheck.client.ui.view.app_views.foodcomponent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.ui.view.dialogs.ReportRecipeDialog
import com.frontend.nutricheck.client.ui.view.widgets.CustomDetailsButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.IngredientList
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.RecipeNutrientChartsWidget
import com.frontend.nutricheck.client.ui.view.widgets.ServingsField
import com.frontend.nutricheck.client.ui.view.widgets.ServingsPicker
import com.frontend.nutricheck.client.ui.view.widgets.ShowErrorMessage
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewMode
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel

/**
 * Displays an overview of a recipe, including its nutritional information, ingredients, and instructions.
 *
 * @param recipeOverviewViewModel The ViewModel that manages the recipe overview state.
 * @param searchViewModel Optional ViewModel for food search functionality.
 * @param reportRecipeViewModel ViewModel for handling recipe reporting.
 * @param onItemClick Callback function to handle item clicks on ingredients.
 * @param onPersist Callback function to handle persistence actions.
 * @param onBack Callback function to handle back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverview(
    recipeOverviewViewModel: RecipeOverviewViewModel,
    searchViewModel: FoodSearchViewModel? = null,
    reportRecipeViewModel: ReportRecipeViewModel,
    onItemClick: (Ingredient) -> Unit = {},
    onPersist: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val recipeOverviewState by recipeOverviewViewModel.recipeOverviewState.collectAsState()
    val uiState by recipeOverviewViewModel.uiState.collectAsState()
    val reportRecipeState by reportRecipeViewModel.reportRecipeState.collectAsState()
    val recipe = recipeOverviewState.recipe

    Scaffold(
        topBar = {
            ViewsTopBar(
                navigationIcon = { NavigateBackButton{ onBack() }},
                title = {
                    Text(
                        text = recipe.name,
                        style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colors.onSurfaceVariant
                    )
                },
                actions = {
                    if (recipeOverviewState.mode is RecipeOverviewMode.General) {
                        CustomDetailsButton(
                            dishItemButton = false,
                            ownedRecipe = recipe.visibility == RecipeVisibility.OWNER,
                            publicRecipe = recipe.visibility == RecipeVisibility.PUBLIC,
                            onOptionClick = { option ->
                                if (option == DropdownMenuOptions.REPORT) {
                                    reportRecipeViewModel.onEvent(
                                        ReportRecipeEvent.ReportClicked(
                                            recipe
                                        )
                                    )
                                }
                                recipeOverviewViewModel.onEvent(
                                    RecipeOverviewEvent.ClickDetailsOption(option)
                                )
                            },
                            expanded = recipeOverviewState.parameters.showDetails,
                            onDetailsClick = {
                                recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickDetails)
                            },
                            onDismissClick = {
                                recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ClickDetails)
                            }
                        )
                    } else {
                        CustomPersistButton {
                            when(recipeOverviewState.mode) {
                                is RecipeOverviewMode.FromSearch -> {
                                    searchViewModel?.onEvent(
                                        SearchEvent.AddFoodComponent(
                                            recipeOverviewState.submitRecipe()
                                        )
                                    )
                                    onPersist()
                                }
                                is RecipeOverviewMode.FromMeal -> {
                                    recipeOverviewViewModel.onEvent(RecipeOverviewEvent.UpdateMealRecipeItem)
                                    onPersist()
                                }
                                else -> null
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            if (uiState is BaseViewModel.UiState.Error) {
                item {
                    ShowErrorMessage(
                        error = (uiState as BaseViewModel.UiState.Error).message,
                        onClick = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ResetErrorState) }
                    )
                }
            }

            item {
                RecipeNutrientChartsWidget(
                    modifier = Modifier.fillMaxWidth(),
                    actualCalories = recipeOverviewState.parameters.calories,
                    actualCarbs = recipeOverviewState.parameters.carbohydrates,
                    actualProtein = recipeOverviewState.parameters.protein,
                    actualFat = recipeOverviewState.parameters.fat,
                    totalCalories = 0.0,
                    totalCarbs = 0.0,
                    totalProtein =  0.0,
                    totalFat = 0.0
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = stringResource(R.string.foodcomponent_servings_label),
                        style = styles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    if (searchViewModel != null) {
                        ServingsPicker(
                            value = recipeOverviewState.parameters.servings,
                            onValueChange = { recipeOverviewViewModel.onEvent(RecipeOverviewEvent.ServingsChanged(it)) }
                        )
                    } else {
                        ServingsField(
                            value = recipeOverviewState.parameters.servings
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.label_ingredients),
                    style = styles.titleMedium,
                    color = colors.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                IngredientList(
                    editing = false,
                    ingredients = recipe.ingredients,
                    onItemClick = { ingredient ->
                        onItemClick(ingredient)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item {
                if (recipe.instructions.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.recipe_description),
                        style = styles.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, colors.outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = recipe.instructions,
                                    style = styles.bodyMedium
                                )
                            }
                        }
                }
            }

            if (reportRecipeState.reporting) {
                item {
                    ReportRecipeDialog(
                        title = stringResource(R.string.report_dialog_title),
                        confirmText = stringResource(R.string.report_dialog_persist),
                        cancelText = stringResource(R.string.cancel),
                        onConfirm = { reportRecipeViewModel.onEvent(ReportRecipeEvent.SendReport) },
                        onDismiss = {
                            reportRecipeViewModel.onEvent(ReportRecipeEvent.DismissDialog)
                        },
                        onCancel = { reportRecipeViewModel.onEvent(ReportRecipeEvent.DismissDialog) },
                        inputText = reportRecipeState.inputText,
                        reportTextPlaceholder = stringResource(R.string.report_text_placeholder),
                        onValueChange = {
                            reportRecipeViewModel.onEvent(
                                ReportRecipeEvent.InputTextChanged(
                                    it
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

