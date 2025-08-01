package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.ui.view.widgets.CustomDetailsButton
import com.frontend.nutricheck.client.ui.view.widgets.IngredientList
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorViewModel

@Composable
fun CreateRecipePage(
    modifier: Modifier = Modifier,
    createRecipeViewModel: RecipeEditorViewModel,
    onItemClick: (Ingredient) -> Unit = {},
    onAddButtonClick: () -> Unit = {},
    onSave: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val draft by createRecipeViewModel.draft.collectAsState()
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Ingredients",
                    style = styles.titleMedium,
                )
                Spacer(Modifier.height(10.dp))
                IngredientList(
                    ingredients = draft.ingredients,
                    onAddButtonClick = { onAddButtonClick() },
                    onItemClick = { ingredient ->
                        onItemClick(ingredient)
                                  },
                    editing = true,
                    trailingContent = { item ->
                        CustomDetailsButton()
                    }
                )

                //TODO: Add error handling for ingredients
                /**if (errorResourceId.value == R.string.create_recipe_error_ingredients) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(errorResourceId!!),
                        color = colors.error,
                        style = styles.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }**/
            }

            item {
                Text(
                    text = "Beschreibung",
                    style = styles.titleMedium,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colors.outline)
                ) {
                    TextField(
                        value = currentDescription,
                        onValueChange = { newDiscription ->
                            createRecipeViewModel.onEvent(RecipeEditorEvent.DescriptionChanged(newDiscription))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}