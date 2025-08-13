package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.ui.view.dialogs.ReportRecipeDialog
import com.frontend.nutricheck.client.ui.view.widgets.CustomDetailsButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomTabRow
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel

@Composable
fun RecipePage(
    modifier: Modifier = Modifier,
    recipePageViewModel: RecipePageViewModel,
    reportRecipeViewModel: ReportRecipeViewModel,
    onAddRecipeClick: () -> Unit = {},
    onItemClick: (Recipe) -> Unit = {}
) {
    val recipePageState by recipePageViewModel.recipePageState.collectAsState()
    val uiState by recipePageViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val reportRecipeState by reportRecipeViewModel.reportRecipeState.collectAsState()

Surface(
    modifier = modifier.fillMaxSize()
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (uiState is BaseViewModel.UiState.Error) {
            ShowErrorMessage(
                error = (uiState as BaseViewModel.UiState.Error).message,
                onClick = { recipePageViewModel.onEvent(RecipePageEvent.ResetErrorState) }
            )
        }
        FoodComponentSearchBar(
            query = recipePageState.query,
            onQueryChange = {
                recipePageViewModel.onEvent(
                    RecipePageEvent.QueryChanged(
                        it
                    )
                )
                            },
            onSearch = { recipePageViewModel.onEvent(RecipePageEvent.SearchOnline) },
            placeholder = { Text(stringResource(R.string.label_search_recipe)) }
        )

        val myRecipes = stringResource(R.string.search_page_label_my_recipes)
        val onlineRecipes = stringResource(R.string.search_page_label_online_recipes)
        CustomTabRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            options = listOf(
                myRecipes,
                onlineRecipes
            ),
            selectedOption = recipePageState.selectedTab,
            onSelect = { index ->
                when (index) {
                    0 -> recipePageViewModel.onEvent(RecipePageEvent.ClickMyRecipes)
                    1 -> recipePageViewModel.onEvent(RecipePageEvent.ClickOnlineRecipes)
                }
            }
        )
        val recipes = if (recipePageState.selectedTab == 0) {
            recipePageState.myRecipes
        } else {
            recipePageState.onlineRecipes
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            when (recipePageState.selectedTab) {
                0 -> {
                    FoodComponentList(
                        foodComponents = recipes,
                        onItemClick = { recipe ->
                            onItemClick(recipe as Recipe)
                        },
                        trailingContent = { foodComponent ->
                            val recipe = foodComponent as Recipe
                            CustomDetailsButton(
                                dishItemButton = true,
                                ownedRecipe = true,
                                onOptionClick = { option ->
                                    recipePageViewModel.onEvent(
                                        RecipePageEvent.ClickDetailsOption(recipe, option)
                                    )
                                },
                                expanded = recipePageState.expandedRecipeId == recipe.id,
                                onDetailsClick = {
                                    recipePageViewModel.onEvent(
                                        RecipePageEvent.ShowDetailsMenu(recipe.id)
                                    )
                                },
                                onDismissClick = {
                                    recipePageViewModel.onEvent(
                                        RecipePageEvent.ShowDetailsMenu(null)
                                    )
                                }
                            )
                        }
                    )
                    ExtendedFloatingActionButton(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.BottomEnd),
                        onClick = { onAddRecipeClick() }
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        Text(
                            text = stringResource(R.string.label_add_recipe),
                            modifier = Modifier.padding(2.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                1 -> {
                    when {
                        recipePageState.query.isBlank() -> {
                            Text(
                                text = stringResource(R.string.label_enter_search_word),
                                style = MaterialTheme.typography.bodyLarge
                            )

                        }

                        uiState == BaseViewModel.UiState.Loading -> {
                            CircularProgressIndicator()
                        }

                        recipePageState.hasSearched &&
                                recipePageState.lastSearchedQuery == recipePageState.query &&
                                recipePageState.onlineRecipes.isEmpty() -> {
                            Text(
                                text = stringResource(R.string.error_search_result_empty),
                                style = MaterialTheme.typography.bodyLarge
                            )

                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    FoodComponentList(
                                        foodComponents = recipePageState.onlineRecipes,
                                        onItemClick = { recipe ->
                                            onItemClick(recipe as Recipe)
                                        },
                                        trailingContent = { foodComponent ->
                                            val recipe = foodComponent as Recipe
                                            CustomDetailsButton(
                                                dishItemButton = true,
                                                publicRecipe = true,
                                                onOptionClick = { option ->
                                                    if (option == DropdownMenuOptions.REPORT) {
                                                        reportRecipeViewModel.onEvent(
                                                            ReportRecipeEvent.ReportClicked(
                                                                recipe
                                                            )
                                                        )
                                                    }
                                                    recipePageViewModel.onEvent(
                                                        RecipePageEvent.ClickDetailsOption(
                                                            recipe,
                                                            option
                                                        )
                                                    )
                                                },
                                                expanded = recipePageState.expandedRecipeId == recipe.id,
                                                onDetailsClick = {
                                                    recipePageViewModel.onEvent(
                                                        RecipePageEvent.ShowDetailsMenu(
                                                            recipe.id
                                                        )
                                                    )
                                                },
                                                onDismissClick = {
                                                    recipePageViewModel.onEvent(
                                                        RecipePageEvent.ShowDetailsMenu(
                                                            null
                                                        )
                                                    )
                                                })
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (reportRecipeState.reporting) {
                        ReportRecipeDialog(
                            onConfirm = {
                                reportRecipeViewModel.onEvent(
                                    ReportRecipeEvent.SendReport
                                )
                            },
                            onCancel = { reportRecipeViewModel.onEvent(ReportRecipeEvent.DismissDialog) },
                            onValueChange = {
                                reportRecipeViewModel.onEvent(
                                    ReportRecipeEvent.InputTextChanged(it)
                                )
                            },
                            reportText = reportRecipeState.inputText
                        )
                    }
                }
            }
        }
    }
}
}


@Composable
private fun ShowErrorMessage(
    title: String = stringResource(R.string.show_error_message_title),
    error: String,
    onClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onClick() },
        title = { Text(title) },
        text = { Text(error) },
        confirmButton = {
            Button(onClick = { onClick() }) {
                Text(stringResource(R.string.label_ok))
            }
        }
    )
}