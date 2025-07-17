package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.AddOptionButton
import com.frontend.nutricheck.client.R

@Composable
fun AddDialog(
    onAddMealClick: () -> Unit = {},
    onAddRecipeClick: () -> Unit = {},
    onScanFoodClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp,
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
                            onAddMealClick }
                    )
                    AddOptionButton(
                        icon = Icons.Default.CameraAlt,
                        label = stringResource(id = R.string.add_dialog_scan_title),
                        onClick = { onScanFoodClick }
                    )
                }
                AddOptionButton(
                    icon = Icons.Default.Create,
                    label = stringResource(id = R.string.add_dialog_recipe_title),
                    onClick = { onAddRecipeClick }
                )
            }
        }
    }
}

@Preview
@Composable
fun AddDialogPreview() {
    AppTheme(darkTheme = true) {
        AddDialog(
            onAddMealClick = {},
            onAddRecipeClick = {},
            onScanFoodClick = {},
            onDismissRequest = {}
        )
    }
}