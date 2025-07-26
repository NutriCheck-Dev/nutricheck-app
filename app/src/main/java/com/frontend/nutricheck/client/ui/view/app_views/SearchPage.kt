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
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view.widgets.CustomAddButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomTabRow
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchEvent

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    searchViewModel: FoodSearchViewModel,
    editRecipeViewModel: EditRecipeViewModel,
    onMealSelected: (DayTime) -> Unit = {},
    onItemClick: (FoodComponent) -> Unit = {},
    onConfirm: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val scrollState = rememberScrollState()
    val uiState by searchViewModel.uiState.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()
    val isFromAddIngredient = searchState.isFromAddIngredient


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (isFromAddIngredient) {
                ViewsTopBar(
                    navigationIcon = { NavigateBackButton(onBack = onBack) },
                    title = {
                        Text(
                            text = "Zutaten Hinzufügen",
                            style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = colors.onSurfaceVariant
                        ) },
                    actions = {
                        IconButton(onConfirm) {
                            Icon(imageVector = Icons.AutoMirrored.Default.ArrowRight, contentDescription = "Weiter")
                        }
                    })
            } else MealSelector(
                dayTime = searchState.dayTime,
                trailingContent = {
                    IconButton(onConfirm) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowRight, contentDescription = "Weiter")
                    }
                },
                onBack = onBack,
                onMealSelected = onMealSelected
            )
        }
    ) { paddingValues ->

        when (uiState) {
            BaseViewModel.UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is BaseViewModel.UiState.Error -> {
                val errorMessage = (uiState as BaseViewModel.UiState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { searchViewModel.onEvent(SearchEvent.Retry) }) {
                        Text("Erneut versuchen")
                    }
                }
            }
            BaseViewModel.UiState.Ready -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(top = 14.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FoodComponentSearchBar(
                        query = searchState.query,
                        onQueryChange = { searchViewModel.onEvent(SearchEvent.QueryChanged(it)) },
                        onSearch = { searchViewModel.onEvent(SearchEvent.Search) }
                    )

                    CustomTabRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        options = listOf("Alle", "Meine Rezepte"),
                        selectedOption = searchState.selectedTab
                    )

                    DishItemList(
                        foodComponents = searchState.results,
                        trailingContent = { item ->
                            CustomAddButton(onClick = {
                                if (isFromAddIngredient) {
                                    editRecipeViewModel.onEvent(
                                        EditRecipeEvent.IngredientAdded(item as FoodProduct)
                                    )
                                } else {
                                    searchViewModel.onEvent(SearchEvent.AddFoodComponent(item))
                                }
                            })
                        },
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}