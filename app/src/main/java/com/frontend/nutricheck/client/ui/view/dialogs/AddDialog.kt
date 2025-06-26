package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.add_components.AddDialogViewModel
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealViewModel
import com.frontend.nutricheck.client.ui.view_model.add_components.AddMealViewModel

@Composable
fun AddDialog(
    modifier: Modifier = Modifier,
    addDialogViewModel: AddDialogViewModel = hiltViewModel(),
    addAiMealViewModel: AddAiMealViewModel = hiltViewModel(),
    addMealViewModel: AddMealViewModel = hiltViewModel(),
    onAddMealClick: () -> Unit = {},
    onAddRecipeClick: () -> Unit = {},
    onScanFoodClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {

}