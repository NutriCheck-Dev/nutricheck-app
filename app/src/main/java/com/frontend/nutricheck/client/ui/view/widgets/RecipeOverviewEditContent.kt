package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.ui.view.dialogs.ActionConfirmationDialog
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeDraft
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverviewEditContent(
    draft: RecipeDraft,
    ingredients: List<Ingredient>,
    onItemClick: (Ingredient) -> Unit,
    onEvent: (RecipeEditorEvent) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    confirmationDialog: Boolean,
    showConfirmationDialog: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    Scaffold(
        topBar = {
            ViewsTopBar(
                navigationIcon = { CustomCloseButton { onCancel() } },
                title = {
                    TextField(
                        value = draft.title,
                        onValueChange = { onEvent(RecipeEditorEvent.TitleChanged(it)) },
                        singleLine = true,
                        textStyle = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            errorContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent)
                    )
                },
                actions = {
                    IconButton(onClick = { showConfirmationDialog() }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Edits",
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
                RecipeNutrientChartsWidget(
                    modifier = Modifier
                        .fillMaxWidth(),
                    recipe = draft.original!!,
                    totalCalories = 0.0,
                    totalCarbs = 0.0,
                    totalProtein = 0.0,
                    totalFat = 0.0
                )
            }

            item {
                Text(
                    text = "Zutaten",
                    style = styles.titleMedium,
                    color = colors.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                IngredientList(
                    editing = true,
                    ingredients = ingredients,
                    onItemClick = { ingredient -> onItemClick(ingredient) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item {
                Text(
                    text = "Beschreibung",
                    style = styles.titleMedium,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colors.outline)
                ) {
                    TextField(
                        value = draft.description,
                        onValueChange = { onEvent(RecipeEditorEvent.DescriptionChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (confirmationDialog) {
            ActionConfirmationDialog(
                title = "Aktion Bestätigen",
                description = "Sind Sie sicher, dass Sie diese Aktion ausführen möchten?",
                confirmText = "Ja",
                cancelText = "Nein",
                icon = Icons.Default.Build,
                onConfirm = {
                    showConfirmationDialog()
                    onSave
                            },
                onCancel = { showConfirmationDialog() }
            )
        }
    }
}