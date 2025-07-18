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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.dialogs.ActionConfirmationDialog
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeDraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverviewEditContent(
    draft: RecipeDraft,
    onEvent: (EditRecipeEvent) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val foodComponents: Set<FoodComponent> =
        draft.ingredients
            .map { it.foodComponent }
            .toSet()

    Scaffold(
        topBar = {
            ViewsTopBar(
                navigationIcon = { CancelButton(onClick = onCancel) },
                title = {
                    TextField(
                        value = draft.title,
                        onValueChange = { onEvent(EditRecipeEvent.TitleChanged(it)) },
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
                    IconButton(onClick = { showConfirmationDialog = true }) {
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
                NutrientChartsWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            item {
                Text(
                    text = "Zutaten",
                    style = styles.titleMedium,
                    color = colors.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                DishItemList(
                    isEditing = true,
                    foodComponents = foodComponents,
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
                        onValueChange = { onEvent(EditRecipeEvent.DescriptionChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (showConfirmationDialog) {
            ActionConfirmationDialog(
                title = "Aktion Bestätigen",
                description = "Sind Sie sicher, dass Sie diese Aktion ausführen möchten?",
                confirmText = "Ja",
                cancelText = "Nein",
                icon = Icons.Default.Build,
                onConfirm = {
                    showConfirmationDialog = false
                    onSave
                            },
                onCancel = { showConfirmationDialog = false }
            )
        }
    }
}