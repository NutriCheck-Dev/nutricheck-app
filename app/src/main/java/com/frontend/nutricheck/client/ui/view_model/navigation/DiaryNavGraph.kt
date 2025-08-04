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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.frontend.nutricheck.client.ui.view.widgets.OverviewSwitcher

enum class DiaryTab(val title: String) {
    HISTORY("History"),
    RECIPES("Recipes")
}
@Composable
fun DiaryNavGraph(
    mainNavController: NavHostController
) {
    var selectedTab by rememberSaveable { mutableStateOf(DiaryTab.HISTORY) }
    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                val selectedTabTitle = stringResource(id = selectedTab.titleResId)
                OverviewSwitcher(
                    options = DiaryTab.entries.map { stringResource(it.titleResId) },
                    selectedOption = selectedTabTitle,
                    onSelect = { option ->
                        selectedTab = DiaryTab.entries.first { selectedTabTitle == option }
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