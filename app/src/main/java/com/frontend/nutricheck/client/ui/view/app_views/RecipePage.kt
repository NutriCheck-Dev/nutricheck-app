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
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.CustomDetailsButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomTabRow
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar

@Composable
fun RecipePage(
    modifier: Modifier = Modifier,
    //viewModel: RecipePageViewModel = hiltViewModel(),
    localRecipes: Set<FoodComponent> = emptySet(),
    remoteRecipes: Set<FoodComponent> = emptySet(),
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


            FoodComponentSearchBar()


            CustomTabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                options = listOf("Meine Rezepte", "Online Rezepte"),
                selectedOption = selectedTab,
            )
            val recipeList = if (selectedTab == 0) localRecipes else remoteRecipes

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(scrollState)
            ) {

                DishItemList(
                    foodComponents = recipeList,
                    trailingContent = { CustomDetailsButton(
                        isOnDishItemButton = true,
                        isOnOwnedRecipe = selectedTab == 0,
                        isOnPublicRecipe = selectedTab == 1
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

@Preview
@Composable
fun RecipePagePreview() {
    AppTheme(
        darkTheme = true
    ) {
        RecipePage(
            localRecipes = setOf(
                Recipe(),
                Recipe(),
                Recipe(),
                Recipe(),
                Recipe()),
            remoteRecipes = setOf(
                Recipe(),
                Recipe(),
                Recipe(),
                Recipe(),
                Recipe(),
                )
        )
    }
}