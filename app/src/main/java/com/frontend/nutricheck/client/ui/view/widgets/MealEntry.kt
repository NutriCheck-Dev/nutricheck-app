package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MealHeader(
    titel: String,
    modifier: Modifier = Modifier,
    calorieCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titel,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun MealFooter(
    text: String = "+ Hinzufügen",
    //modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = Color(0xFF4580FF),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )

        IconButton(onClick = onMoreClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Mehr",
                tint = Color(0xFF9F9F9F)
            )
        }
    }
}

@Preview
@Composable
fun MealFooterPreview() {
    MealFooter()
}