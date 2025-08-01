package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.frontend.nutricheck.client.ui.view.app_views.DiaryTab
import com.frontend.nutricheck.client.ui.view.widgets.OverviewSwitcher
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel


sealed class DiaryScreens(val route: String) {
    object HistoryPage : DiaryScreens("history_page")
    object RecipePage : DiaryScreens("recipe_page")
    object DiaryPage : DiaryScreens("diary_page")

}

@Composable
fun DiaryNavGraph(
    mainNavController: NavHostController
) {
    var selectedTab by rememberSaveable { mutableStateOf(DiaryTab.RECIPES) }
    val recipePageViewModel: RecipePageViewModel = hiltViewModel()
    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                OverviewSwitcher(
                    options = DiaryTab.entries.map { it.title },
                    selectedOption = selectedTab.title,
                    onSelect = { option ->
                        selectedTab = DiaryTab.entries.first { it.title == option }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 14.dp)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                DiaryTab.HISTORY -> HistoryPageNavGraph(mainNavController)
                DiaryTab.RECIPES -> RecipePageNavGraph(mainNavController)
            }
        }
    }
}