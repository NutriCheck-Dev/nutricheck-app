package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    meal: String = "Mahlzeit auswählen",
    onOptionSelected: (String) -> Unit,
    onDishClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {}