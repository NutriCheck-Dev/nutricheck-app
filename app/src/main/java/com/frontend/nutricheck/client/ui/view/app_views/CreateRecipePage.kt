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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view.widgets.CustomDetailsButton
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.recipe.create.CreateRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.create.CreateRecipeViewModel

@Composable
fun CreateRecipePage(
    modifier: Modifier = Modifier,
    createRecipeViewModel: CreateRecipeViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val draft by createRecipeViewModel.createdRecipeDraft.collectAsState()
    val currentTitle = draft?.title.orEmpty()
    val ingredients = draft?.ingredients ?: emptySet()
    val currentIngredients = ingredients.map { it.foodProduct }.toSet()
    val currentDescription = draft?.description.orEmpty()
    val errorResourceId by createRecipeViewModel.errorState.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        topBar = {
            ViewsTopBar(
                navigationIcon = { NavigateBackButton(onBack = onBack) },
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
                        createRecipeViewModel.onEvent(CreateRecipeEvent.TitleChanged(new))
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
                        createRecipeViewModel.onEvent(CreateRecipeEvent.RecipeSaved)
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
                DishItemList(
                    foodComponents = currentIngredients,
                    isEditing = true,
                    trailingContent = { item ->
                        CustomDetailsButton()
                    }
                )

                if (errorResourceId == R.string.create_recipe_error_ingredients) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(errorResourceId!!),
                        color = colors.error,
                        style = styles.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
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
                            createRecipeViewModel.onEvent(CreateRecipeEvent.DescriptionChanged(newDiscription))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
