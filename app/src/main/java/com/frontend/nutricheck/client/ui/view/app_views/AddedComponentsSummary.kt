package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentList
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchUiState

@Composable
fun AddedComponentsSummary(
    modifier: Modifier = Modifier,
    searchViewModel: FoodSearchViewModel,
    recipeEditorViewModel: RecipeEditorViewModel,
    onItemClick: (FoodComponent) -> Unit = {},
    onSave: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val scrollState = rememberScrollState()
    val searchState by searchViewModel.searchState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (searchState is SearchUiState.AddIngredientState) {
                ViewsTopBar(
                    navigationIcon = { NavigateBackButton { onBack() } },
                    title = {
                        Text(
                            text = "Zutaten Hinzufügen",
                            style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = colors.onSurfaceVariant
                        ) },
                    actions = {
                        CustomPersistButton {
                            onSave()
                        }
                    })
            } else MealSelector(
                dayTime = if (searchState is SearchUiState.AddComponentsToMealState)
                    (searchState as SearchUiState.AddComponentsToMealState).dayTime
                else null,
                expanded = searchState.parameters.expanded,
                onExpandedChange = { searchViewModel.onEvent(SearchEvent.MealSelectorClick) },
                trailingContent = { CustomPersistButton { onSave() } },
                onMealSelected = { dayTime ->
                    searchViewModel.onEvent(SearchEvent.DayTimeChanged(dayTime))},
                onBack = { onBack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 14.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            FoodComponentList(
                foodComponents = searchState.parameters.addedComponents.map { it.second },
                trailingContent = { item ->
                    CustomCloseButton(onClick = {
                            searchViewModel.onEvent(SearchEvent.RemoveFoodComponent(item))
                    })
                },
                onItemClick = onItemClick
            )
        }
    }
}