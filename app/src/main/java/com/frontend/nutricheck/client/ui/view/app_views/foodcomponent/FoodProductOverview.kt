package com.frontend.nutricheck.client.ui.view.app_views.foodcomponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.ServingSize
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.ServingsPicker
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.FoodProductNutrientChartsWidget
import com.frontend.nutricheck.client.ui.view.widgets.ServingSizeDropdown
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar

@Composable
fun FoodProductOverview(
    draft: FoodProduct = FoodProduct(),
    isFromIngredient: Boolean = false,
    onCancel: () -> Unit = { },
    onPersist: (FoodProduct) -> Unit = { },
    onDropdownItemClick: (ServingSize) -> Unit = { },
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val actions = if (isFromIngredient) CustomPersistButton({ onPersist }) else null
    val servingSizes = listOf(ServingSize.entries)
    var expanded by remember { mutableStateOf(false) }
    var count by remember { mutableIntStateOf(1) }

    Scaffold(
        topBar = {
            ViewsTopBar(
                navigationIcon = { CustomCloseButton(onClick = onCancel) },
                title = {
                    Text(
                        text = draft.name,
                        style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colors.onSurfaceVariant
                    )
                },
                actions = { actions }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            FoodProductNutrientChartsWidget(
                foodProduct = draft,
                modifier = Modifier.wrapContentHeight()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Serving Size:",
                    style = styles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                ServingSizeDropdown()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Servings:",
                    style = styles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                ServingsPicker(
                    value = count,
                    range = 1..200,
                    onValueChange = { count = it }
                )
            }
        }
    }
}

@Preview
@Composable
fun FoodProductOverviewPreview() {
    AppTheme(darkTheme = true) {
        FoodProductOverview()
    }
}