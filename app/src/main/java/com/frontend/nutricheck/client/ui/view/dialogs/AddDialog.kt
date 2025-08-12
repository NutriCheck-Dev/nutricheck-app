package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.view.widgets.AddOptionButton
import com.frontend.nutricheck.client.R

@Composable
fun AddDialog(
    onAddMealClick: () -> Unit = {},
    onAddRecipeClick: () -> Unit = {},
    onScanFoodClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
        ModalBottomSheet(
            onDismissRequest = { onDismissRequest() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null, // Disable the drag handle
            scrimColor = Color.Black.copy(alpha = 0.3f) // Semi-transparent background
            ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                color = colors.surfaceVariant,
                contentColor = colors.onSurfaceVariant
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 43.dp),
                    verticalArrangement = Arrangement.spacedBy(33.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(26.dp)
                    ) {
                        AddOptionButton(
                            icon = Icons.Default.Search,
                            label = stringResource(id = R.string.add_dialog_meal_title),
                            onClick = {
                                onAddMealClick()
                            }
                        )
                        AddOptionButton(
                            icon = Icons.Default.CameraAlt,
                            label = stringResource(id = R.string.add_dialog_scan_title),
                            onClick = { onScanFoodClick() }
                        )
                    }
                    AddOptionButton(
                        icon = Icons.Default.Create,
                        label = stringResource(id = R.string.add_dialog_recipe_title),
                        onClick = { onAddRecipeClick() }
                    )
                }
            }
        }
    }
