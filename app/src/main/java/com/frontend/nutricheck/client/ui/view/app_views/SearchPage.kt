package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    meal: String = "Mahlzeit auswählen",
    onOptionSelected: (String) -> Unit,
    onDishClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {}