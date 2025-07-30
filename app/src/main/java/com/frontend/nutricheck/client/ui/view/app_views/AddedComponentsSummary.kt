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
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchEvent

@Composable
fun AddedComponentsSummary(
    modifier: Modifier = Modifier,
    searchViewModel: FoodSearchViewModel,
    editRecipeViewModel: EditRecipeViewModel,
    onItemClick: (FoodComponent) -> Unit = {},
    onSave: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val scrollState = rememberScrollState()
    val searchState by searchViewModel.searchState.collectAsState()
    val editRecipeState by editRecipeViewModel.editRecipeDraft.collectAsState()
    val isFromAddIngredient = searchState.fromAddIngredient

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
                    actions = { CustomPersistButton(onSave) })
            } else MealSelector(
                trailingContent = { CustomPersistButton(onSave) }
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
            DishItemList(
                foodComponents = if (isFromAddIngredient) {
                    editRecipeState!!.viewIngredients
                } else {
                    searchState.addedComponents.map { it.second }
                       },
                trailingContent = { item ->
                    CustomCloseButton(onClick = {
                        if (isFromAddIngredient) {
                            editRecipeViewModel.onEvent(
                                EditRecipeEvent.IngredientRemovedInSummary(item as FoodProduct)
                            )
                        } else {
                            searchViewModel.onEvent(SearchEvent.RemoveFoodComponent(item))
                        }
                    })
                },
                onItemClick = onItemClick
            )
        }
    }
}