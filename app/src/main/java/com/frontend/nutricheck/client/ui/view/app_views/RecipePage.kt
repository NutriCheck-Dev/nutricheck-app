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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view.widgets.CustomDetailsButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomTabRow
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel

@Composable
fun RecipePage(
    modifier: Modifier = Modifier,
    recipePageViewModel: RecipePageViewModel = hiltViewModel(),
    onAddRecipeClick: () -> Unit = {}
) {
    val recipePageState by recipePageViewModel.recipePageState.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {


            FoodComponentSearchBar()


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
                recipePageState.myRecipes.toSet()
            } else {
                recipePageState.onlineRecipes.toSet()
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(scrollState)
            ) {

                DishItemList(
                    foodComponents = recipes,
                    trailingContent = { CustomDetailsButton(
                        isOnDishItemButton = true,
                        isOnOwnedRecipe = recipePageState.selectedTab == 0,
                        isOnPublicRecipe = recipePageState.selectedTab == 1,
                    ) }
                )

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