package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MealSelectionDialog(
    modifier: Modifier = Modifier,
    currentMeal: String = "Breakfast",
    meals: List<String> = listOf("Breakfast", "Lunch", "Dinner", "Snack"),
    onMealSelectedClick: (String) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {}