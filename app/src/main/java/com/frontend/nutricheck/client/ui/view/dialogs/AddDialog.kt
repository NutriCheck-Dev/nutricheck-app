package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view.widgets.AddOptionButton
import com.frontend.nutricheck.client.ui.view_model.add_components.AddDialogViewModel
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealViewModel
import com.frontend.nutricheck.client.ui.view_model.add_components.AddMealViewModel

@Composable
fun AddDialog(
    modifier: Modifier = Modifier,
    //addDialogViewModel: AddDialogViewModel = hiltViewModel(),
    //addAiMealViewModel: AddAiMealViewModel = hiltViewModel(),
    //addMealViewModel: AddMealViewModel = hiltViewModel(),
    onAddMealClick: () -> Unit = {},
    onAddRecipeClick: () -> Unit = {},
    onScanFoodClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
                //.padding(horizontal = 34.dp, vertical = 43.dp),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp,
            color = Color(0xFF121212)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 43.dp),
                verticalArrangement = Arrangement.spacedBy(43.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(26.dp)
                ) {
                    AddOptionButton(
                        icon = Icons.Default.Search,
                        label = "Mahlzeit hinzufügen",
                        onClick = { onAddMealClick }
                    )
                    AddOptionButton(
                        icon = Icons.Default.Build,
                        label = "Essen Scannen",
                        onClick = { onScanFoodClick }
                    )
                }
                AddOptionButton(
                    icon = Icons.Default.Create,
                    label = "Rezept hinzufügen",
                    onClick = { onAddRecipeClick }
                )
            }
        }
    }
}

@Preview
@Composable
fun AddDialogPreview() {
    AddDialog(
        onAddMealClick = {},
        onAddRecipeClick = {},
        onScanFoodClick = {},
        onDismissRequest = {}
    )
}