package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.widgets.OverviewSwitcher
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

enum class DiaryTab(val title: String) {
    HISTORY("Verlauf"),
    RECIPES("Rezepte")
}

@Composable
fun DiaryPage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
) {
    var selectedTab by rememberSaveable { mutableStateOf(DiaryTab.RECIPES) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = { Surface(
            color = Color(0xFF121212),
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            OverviewSwitcher(
                options= DiaryTab.entries.map { it.title },
                selectedOption = selectedTab.title,
                onSelect = { option ->
                    selectedTab = DiaryTab.entries.first { it.title == option }
                },
                modifier = Modifier.fillMaxWidth()
            )
        } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Spacer(Modifier.height(31.dp))
            when (selectedTab) {
                DiaryTab.HISTORY -> HistoryPage(actions = actions)
                DiaryTab.RECIPES -> RecipePage(actions = actions)
            }
        }
    }
}

@Preview
@Composable
fun DiaryPagePreview() {
    val navController = rememberNavController()
    val previewActions = NavigationActions(navController)

    DiaryPage(actions = previewActions)
}