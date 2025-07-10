package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.CustomTabRow
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun RecipePage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    //viewModel: RecipePageViewModel = hiltViewModel(),
    title: String = "Rezepte",
    localRecipes: @Composable () -> Unit = {},
    remoteRecipes: @Composable () -> Unit = {},
    onRecipeSelected: (String) -> Unit = {},
    onDetailsCick: (String) -> Unit = {},
    onAddRecipeClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.TopCenter
            ) {
                FoodComponentSearchBar()
            }

            CustomTabRow(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                options = listOf("Meine Rezepte", "Online Rezepte"),
                selectedOption = selectedTab,
            )
            val data = if (selectedTab == 0) localRecipes else remoteRecipes

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(scrollState)
            ) {

                data()

                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd),
                    onClick = onAddRecipeClick
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Text(
                        text = "Rezept hinzufügen",
                        modifier = Modifier.padding(2.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun RecipePagePreview() {
    val navController = rememberNavController()
    val previewActions = NavigationActions(navController)
    AppTheme(
        darkTheme = true
    ) {
        RecipePage(
            localRecipes = {
                DishItemList(
                    list = listOf(
                        Recipe(),
                        Recipe(),
                        Recipe(),
                        Recipe(),
                        Recipe(),
                    )
                )
            },
            remoteRecipes = {
                DishItemList(
                    list = listOf(
                        Recipe(),
                        Recipe(),
                        Recipe(),
                        Recipe(),
                        Recipe(),
                    )
                )
            },
            actions = previewActions,
        )
    }
}