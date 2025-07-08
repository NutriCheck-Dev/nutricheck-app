package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    //actions: NavigationActions,
    //searchViewModel: FoodSearchViewModel = hiltViewModel(),
    meal: String = "Mahlzeit auswählen",
    onOptionSelected: (String) -> Unit,
    onDishClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                MealSelector()
            } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(31.dp))

            FoodComponentSearchBar()

            DishItemList()
        }
    }
    /**Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.background,
        contentColor = colors.onBackground
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MealSelector()

            FoodComponentSearchBar()

            DishItemList()
        }
    }**/
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