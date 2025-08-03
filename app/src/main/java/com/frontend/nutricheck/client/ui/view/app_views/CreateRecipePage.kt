package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorViewModel

@Composable
fun CreateRecipePage(
    modifier: Modifier = Modifier,
    createRecipeViewModel: RecipeEditorViewModel,
    onItemClick: (FoodComponent) -> Unit = {},
    onSave: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val uiState by createRecipeViewModel.uiState.collectAsState()
    val draft by createRecipeViewModel.draft.collectAsState()
    val currentTitle = draft.title
    val currentDescription = draft.description
    val scrollState = rememberScrollState()

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
                            text = "Rezeptname",
                            style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.onSurfaceVariant
                        )
                    },
                    onValueChange = { new ->
                        createRecipeViewModel.onEvent(RecipeEditorEvent.TitleChanged(new))
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
                        createRecipeViewModel.onEvent(RecipeEditorEvent.SaveRecipe)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .scrollable(state = scrollState, orientation = Orientation.Vertical),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Beschreibung",
                style = styles.titleMedium,
                )
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, colors.outline)
            ) {
                TextField(
                    value = currentDescription,
                    onValueChange = { newDescription ->
                        createRecipeViewModel.onEvent(RecipeEditorEvent.DescriptionChanged(newDescription))
                        },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

                SearchPage(
                    onItemClick = { onItemClick(it) },
                    expand = draft.expanded,
                    addedComponents = draft.ingredients.map { it.second },
                    query = draft.query,
                    searchResults = draft.results,
                    onSearchClick = { createRecipeViewModel.onEvent(RecipeEditorEvent.SearchIngredients) },
                    onQueryChange = { createRecipeViewModel.onEvent(RecipeEditorEvent.QueryChanged(it)) },
                    addFoodComponent = {
                        createRecipeViewModel.onEvent(
                            RecipeEditorEvent.IngredientAdded(
                                Pair(1.0, it)
                            )
                        )
                    },
                    removeFoodComponent = {
                        createRecipeViewModel.onEvent(
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