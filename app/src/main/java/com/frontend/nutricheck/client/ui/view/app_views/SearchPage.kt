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
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.CustomAddButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomTabRow
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchUiState

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    searchViewModel: FoodSearchViewModel,
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


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (searchState is SearchUiState.AddIngredientState) {
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
                dayTime = if (searchState is SearchUiState.AddComponentsToMealState)
                        (searchState as SearchUiState.AddComponentsToMealState).dayTime
                else null,
                trailingContent = {
                    IconButton(onConfirm) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowRight, contentDescription = "Weiter")
                    }
                },
                onBack = { onBack() },
                onMealSelected = { daytime ->
                    searchViewModel.onEvent(SearchEvent.DayTimeChanged(daytime))
                }
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
                        query = searchState.parameters.query,
                        onQueryChange = { searchViewModel.onEvent(SearchEvent.QueryChanged(it)) },
                        onSearch = { searchViewModel.onEvent(SearchEvent.Search) }
                    )

                    CustomTabRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        options = listOf("Alle", "Meine Rezepte"),
                        selectedOption = searchState.parameters.selectedTab
                    )

                    FoodComponentList(
                        foodComponents = searchState.parameters.results,
                        trailingContent = { item ->
                            CustomAddButton(onClick = {
                                searchViewModel.onEvent(SearchEvent.AddFoodComponent(Pair(1.0, item)))
                            })
                        },
                        onItemClick = { item ->
                            onItemClick(item)
                        }
                    )
                }
            }
        }
    }
}