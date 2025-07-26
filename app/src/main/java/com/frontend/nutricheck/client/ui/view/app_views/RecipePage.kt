package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.CustomDetailsButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomTabRow
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel

@Composable
fun RecipePage(
    modifier: Modifier = Modifier,
    recipePageViewModel: RecipePageViewModel = hiltViewModel(),
    onAddRecipeClick: () -> Unit = {},
    onItemClick: (FoodComponent) -> Unit = {}
) {
    val recipePageState by recipePageViewModel.recipePageState.collectAsState()
    val uiState by recipePageViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (uiState) {
                BaseViewModel.UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is BaseViewModel.UiState.Error -> {
                    val message = (uiState as BaseViewModel.UiState.Error).message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Fehler: $message")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { recipePageViewModel.onEvent(RecipePageEvent.SearchOnline) }) {
                                Text("Erneut versuchen")
                            }
                        }
                    }
                }
                BaseViewModel.UiState.Ready -> {
                    FoodComponentSearchBar(
                        query = recipePageState.query,
                        onQueryChange = { recipePageViewModel.onEvent(RecipePageEvent.QueryChanged(it))},
                        onSearch = { recipePageViewModel.onEvent(RecipePageEvent.SearchOnline) },
                        placeholder = { Text("Rezept suchen") }
                    )


                    CustomTabRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        options = listOf("Meine Rezepte", "Online Rezepte"),
                        selectedOption = recipePageState.selectedTab,
                        onSelect = { index ->
                            when (index) {
                                0 -> recipePageViewModel.onEvent(RecipePageEvent.ClickMyRecipes)
                                1 -> recipePageViewModel.onEvent(RecipePageEvent.ClickOnlineRecipes)
                            }
                        }
                    )
                    val recipes = if (recipePageState.selectedTab == 0) {
                        recipePageState.myRecipes
                    } else {
                        recipePageState.onlineRecipes
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(8.dp)
                            .verticalScroll(scrollState)
                    ) {
                        when (recipePageState.selectedTab) {
                            0 -> DishItemList(
                                foodComponents = recipes,
                                onItemClick = onItemClick,
                                trailingContent = {
                                    CustomDetailsButton(
                                        isOnDishItemButton = true,
                                        isOnOwnedRecipe = true,
                                    )
                                }
                            )
                            1 -> {
                                if (recipePageState.query.isBlank()) {
                                    Text(
                                        text = "Bitte geben Sie einen Suchbegriff ein",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    DishItemList(
                                        foodComponents = recipePageState.onlineRecipes,
                                        onItemClick = onItemClick,
                                        trailingContent = {
                                            CustomDetailsButton(
                                                isOnDishItemButton = true,
                                                isOnPublicRecipe = true
                                            )
                                        }
                                    )
                                }
                            }
                        }

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
    }
}