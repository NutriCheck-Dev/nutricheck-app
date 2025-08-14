package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.widgets.OverviewSwitcher

enum class DiaryTab(val title: String) {
    HISTORY("History"),
    RECIPES("Recipes")
}
@Composable
fun DiaryNavGraph(
    mainNavController: NavHostController
) {
    val historyPageNavController = rememberNavController()
    val recipePageNavController = rememberNavController()
    var selectedTab by rememberSaveable { mutableStateOf(DiaryTab.HISTORY) }

    val headerVisibleRoutes = remember {
        setOf(
            HistoryPageScreens.HistoryPage.route,
            RecipePageScreens.RecipePage.route
        )
    }

    val historyBackStackEntry by historyPageNavController.currentBackStackEntryAsState()
    val recipeBackStackEntry by recipePageNavController.currentBackStackEntryAsState()

    val currentRoute = when (selectedTab) {
        DiaryTab.HISTORY -> historyBackStackEntry?.destination?.route?.substringBefore("?")
        DiaryTab.RECIPES -> recipeBackStackEntry?.destination?.route?.substringBefore("?")
    }

    val showHeader = currentRoute in headerVisibleRoutes
    Scaffold(
        topBar = {
            if (showHeader) {
                OverviewSwitcher(
                    options = DiaryTab.entries.map { it.title },
                    selectedOption = selectedTab.title,
                    onSelect = { option ->
                        selectedTab = DiaryTab.entries.first { it.title == option }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = if (showHeader) 14.dp else 0.dp)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                DiaryTab.HISTORY -> HistoryPageNavGraph(mainNavController, historyPageNavController)
                DiaryTab.RECIPES -> RecipePageNavGraph(mainNavController, recipePageNavController)
            }
        }
    }
}