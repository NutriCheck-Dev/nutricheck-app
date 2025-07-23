package com.frontend.nutricheck.client.ui.view.app_views.foodcomponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.ServingSize
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.FoodProductNutrientChartsWidget
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar

@Composable
fun FoodProductOverview(
    draft: FoodProduct = FoodProduct(),
    isFromIngredient: Boolean = false,
    currentServingSize: ServingSize = ServingSize.entries.first(),
    onCancel: () -> Unit = { },
    onPersist: (FoodProduct) -> Unit = { },
    onDropdownItemClick: (ServingSize) -> Unit = { },
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val actions = if (isFromIngredient) CustomPersistButton({ onPersist }) else null
    val servingSizes = listOf(ServingSize.entries)
    var expanded by remember { mutableStateOf(false) }

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
                .padding(16.dp)
        ) {

            FoodProductNutrientChartsWidget(
                foodProduct = draft,
                modifier = Modifier.fillMaxSize()
            )

            Row {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    Text(
                        text = currentServingSize.getDisplayName(),
                        style = styles.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
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