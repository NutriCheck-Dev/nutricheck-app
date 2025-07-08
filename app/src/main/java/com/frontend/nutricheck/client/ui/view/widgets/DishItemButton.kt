package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//This file represents a DishItemButton composable function that displays a button for a dish item.
@Composable
fun DishItemButton(
    modifier: Modifier = Modifier,
    title: String = "Gericht",
    subtitle: String = "0 kcal, Portionsgröße",
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1F1F1F),
        tonalElevation = 0.dp,
        onClick = onClick
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = Color(0xFF6E6E6E)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFBDBDBD),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            trailingContent?.let {
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    it()
                }
            }
        }
    }
}

@Preview
@Composable
fun DishItemButtonPreview() {
    DishItemButton(
        trailingContent = {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Hinzufügen",
                    tint = Color.Black
                )
            }
        },
        onClick = {}
    )
}