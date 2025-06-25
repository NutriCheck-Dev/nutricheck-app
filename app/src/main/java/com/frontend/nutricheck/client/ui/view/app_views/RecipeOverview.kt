package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun RecipeOverview(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    title: String = "Rezept",
    onFoodClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {}