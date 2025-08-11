package com.frontend.nutricheck.client.ui.view_model.navigation

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.frontend.nutricheck.client.R
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
    mainNavController: NavHostController
) {
    var selectedTab by rememberSaveable { mutableStateOf(DiaryTab.HISTORY) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    val context = mainNavController.context
    Scaffold(
        topBar = {
                OverviewSwitcher(
                    options = DiaryTab.entries.map { it.getDescription(context) },
                    selectedOption = selectedTab.getDescription(context),
                    onSelect = { option ->
                        selectedTab = DiaryTab.entries.first { it.getDescription(context) == option }
                    }
                )
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