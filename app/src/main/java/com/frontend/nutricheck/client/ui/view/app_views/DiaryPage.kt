package com.frontend.nutricheck.client.ui.view.app_views

import HistoryPage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.OverviewSwitcher

enum class DiaryTab(val title: String) {
    HISTORY("Verlauf"),
    RECIPES("Rezepte")
}

@Composable
fun DiaryPage(
    modifier: Modifier = Modifier,
    //historyViewModel: HistoryViewModel = hiltViewModel(),
    //recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
) {
    MaterialTheme.colorScheme
    var selectedTab by rememberSaveable { mutableStateOf(DiaryTab.RECIPES) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { 
            Surface(
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
                .padding(top = 14.dp)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                DiaryTab.HISTORY -> HistoryPage()
                DiaryTab.RECIPES -> RecipePage(
                    localRecipes = setOf(
                        Recipe(),
                        Recipe(),
                        Recipe(),
                        Recipe()
                    ),
                    remoteRecipes = setOf(
                        Recipe(),
                        Recipe(),
                        Recipe(),
                        Recipe()
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun DiaryPagePreview() {
    AppTheme(darkTheme = true) {
        DiaryPage()
    }
}