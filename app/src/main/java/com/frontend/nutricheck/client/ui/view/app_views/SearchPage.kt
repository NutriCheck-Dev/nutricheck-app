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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.CustomTabRow
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchEvent

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    searchViewModel: FoodSearchViewModel = hiltViewModel(),
    meal: String = "Mahlzeit auswählen",
    onOptionSelected: (String) -> Unit,
    onDishClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    foodList: Set<FoodComponent> = emptySet(),
    onBack: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(0) }
    var isAddIngredient by remember { mutableStateOf(true) }
    val uiState by searchViewModel.uiState.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()

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

        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (isAddIngredient) {
                ViewsTopBar(
                    navigationIcon = { NavigateBackButton(onBack = onBack) },
                    title = {
                        Text(
                            text = "Zutaten Hinzufügen",
                            style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = colors.onSurfaceVariant
                        ) })
            } else MealSelector()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 14.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            //Spacer(modifier = Modifier.height(31.dp))
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
                selectedOption = selectedTab
            )

            DishItemList(foodComponents = searchState.results.toSet())
        }
    }
}

@Preview
@Composable
fun SearchPagePreview() {
    AppTheme(darkTheme = true) {
        SearchPage(
            onOptionSelected = {},
            onDishClick = {},
            onAddClick = {},
            onBack = {}
        )
    }
}