package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme

//This file represents a DishItemButton composable function that displays a button for a dish item.
@Composable
fun DishItemButton(
    modifier: Modifier = Modifier,
    title: String = "Gericht",
    subtitle: String = "0 kcal, Portionsgröße",
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = title,
                    style = styles.bodyLarge
                )

                VerticalDivider(
                    modifier = Modifier.size(1.dp, 24.dp),
                    color = colors.outline
                )

                Text(
                    text = subtitle,
                    style = styles.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            trailingContent?.invoke()
            }

    }
}

@Preview
@Composable
fun DishItemButtonPreview() {
    AppTheme(darkTheme = true) {
    DishItemButton(
        trailingContent = { CustomAddButton() },
        onClick = {}
    )
        }
}