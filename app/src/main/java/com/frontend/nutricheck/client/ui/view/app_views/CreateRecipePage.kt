package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.CustomEditButton
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar

@Composable
fun CreateRecipePage(
    modifier: Modifier = Modifier,
    title: String = "Create Recipe",
    ingredients: List<FoodComponent> = emptyList(),
    description: String = "",
    onSave: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    var titleText by remember { mutableStateOf(title) }
    var ingredients by remember { mutableStateOf(ingredients) }
    var descriptionText by remember { mutableStateOf(description) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        topBar = {
            ViewsTopBar(
                navigationIcon = { NavigateBackButton(onBack = onBack) },
                title = { Text(
                    text = titleText,
                    style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.onSurfaceVariant
                ) },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Recipe",
                            tint = colors.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Ingredients",
                    style = styles.titleMedium,
                )
                Spacer(Modifier.height(10.dp))
                DishItemList(
                    list = ingredients,
                    isEditing = true,
                    trailingContent = { CustomEditButton() }
                )
            }

            item {
                Text(
                    text = "Beschreibung",
                    style = styles.titleMedium,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colors.outline)
                ) {
                    TextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateRecipePagePreview() {
    AppTheme(darkTheme = true) {
        CreateRecipePage(
            title = "Sample Recipe",
            description = "This is a sample recipe description.",
            onSave = {},
            onBack = {}
        )
    }
}