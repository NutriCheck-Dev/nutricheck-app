package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.runtime.Composable

@Composable
fun RateRecipeDialog(
    recipeId: String,
    onDismiss: () -> Unit,
    onRate: (Int) -> Unit
) {}