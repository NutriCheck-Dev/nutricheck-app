package com.frontend.nutricheck.client.ui.view_model.navigation

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.ui.view.widgets.OverviewSwitcher

enum class DiaryTab(val stringResId: Int) {
    HISTORY(R.string.label_history),
    RECIPES(R.string.label_recipes);

    fun getDescription(context: Context): String {
        return context.getString(stringResId)
    }
}
@Composable
fun DiaryNavGraph(
    destination: DiaryGraphDestination = DiaryGraphDestination.HISTORY_RELATED
) {
    val context = LocalContext.current
    val historyPageNavController = rememberNavController()
    val recipePageNavController = rememberNavController()
    var selectedTab by rememberSaveable { mutableStateOf(DiaryTab.HISTORY) }

    LaunchedEffect(destination) {
        selectedTab = when (destination) {
            DiaryGraphDestination.RECIPE_RELATED -> DiaryTab.RECIPES
            DiaryGraphDestination.HISTORY_RELATED -> DiaryTab.HISTORY
        }
    }

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
                    options = DiaryTab.entries.map { it.getDescription(context) },
                    optionIds = DiaryTab.entries.map { it.name },
                    selectedOption = selectedTab.getDescription(context),
                    onSelect = { option ->
                        selectedTab = DiaryTab.entries.first { it.getDescription(context) == option }
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
                DiaryTab.HISTORY -> HistoryPageNavGraph(historyPageNavController)
                DiaryTab.RECIPES -> RecipePageNavGraph(recipePageNavController)
            }
        }
    }
}