package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MealSelector(
    modifier: Modifier = Modifier,
    title: String = "Mahlzeit auswählen",
    mealOptions: List<String> = listOf("Frühstück", "Mittagessen", "Abendessen", "Snack"),
    onMealSelected: (String) -> Unit
) {

}