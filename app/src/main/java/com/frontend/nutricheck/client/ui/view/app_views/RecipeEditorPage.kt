package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ServingsPicker
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorViewModel

@Composable
fun RecipeEditorPage(
    modifier: Modifier = Modifier,
    recipeEditorViewModel: RecipeEditorViewModel,
    onItemClick: (FoodComponent) -> Unit = {},
    onSave: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val uiState by recipeEditorViewModel.uiState.collectAsState()
    val draft by recipeEditorViewModel.draft.collectAsState()
    val currentTitle = draft.title
    val currentDescription = draft.description

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        topBar = {
            ViewsTopBar(
                navigationIcon = { NavigateBackButton(onBack = { onBack() }) },
                title = { TextField(
                    value = currentTitle,
                    placeholder = {
                        Text(
                            text = if (draft.original != null) draft.original!!.name else
                                stringResource(R.string.recipe_name_placeholder),
                            style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.onSurfaceVariant
                        )
                    },
                    onValueChange = { new ->
                        recipeEditorViewModel.onEvent(RecipeEditorEvent.TitleChanged(new))
                    },
                    singleLine = true,
                    textStyle = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        errorContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    )
                ) },
                actions = {
                    IconButton(onClick = {
                        onSave()
                        recipeEditorViewModel.onEvent(RecipeEditorEvent.SaveRecipe)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Recipe",
                            tint = colors.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (uiState is BaseViewModel.UiState.Error) {
                item {
                    ShowErrorMessage(
                        error = (uiState as BaseViewModel.UiState.Error).message,
                        onClick = {
                            recipeEditorViewModel.onEvent(RecipeEditorEvent.ResetErrorState)
                        }
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = "Servings:",
                        style = styles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ServingsPicker(
                        value = draft.servings,
                        range = 1..200,
                        onValueChange = {
                            recipeEditorViewModel.onEvent(
                                RecipeEditorEvent.ServingsChanged(
                                    it
                                )
                            )
                        }
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.recipe_description),
                    style = styles.titleMedium,
                )
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colors.outline)
                ) {
                    TextField(
                        value = currentDescription,
                        onValueChange = { newDescription ->
                            recipeEditorViewModel.onEvent(
                                RecipeEditorEvent.DescriptionChanged(
                                    newDescription
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            item {
                SearchPage(
                    onItemClick = { onItemClick(it) },
                    expand = draft.expanded,
                    addedComponents = draft.ingredients.map { it },
                    query = draft.query,
                    searchResults = draft.results,
                    onSearchClick = { recipeEditorViewModel.onEvent(RecipeEditorEvent.SearchIngredients) },
                    onQueryChange = {
                        recipeEditorViewModel.onEvent(
                            RecipeEditorEvent.QueryChanged(
                                it
                            )
                        )
                    },
                    addFoodComponent = {
                        recipeEditorViewModel.onEvent(
                            RecipeEditorEvent.IngredientAdded(it)
                        )
                    },
                    removeFoodComponent = {
                        recipeEditorViewModel.onEvent(
                            RecipeEditorEvent.IngredientRemoved(
                                it
                            )
                        )
                    },
                    showTabRow = false,
                    isLoading = uiState == BaseViewModel.UiState.Loading
                )
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