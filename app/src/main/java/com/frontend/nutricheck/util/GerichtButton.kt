package com.frontend.nutricheck.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun GerichtButton(
    modifier: Modifier = Modifier,
    title: String = "Gericht",
    subtitle: String = "0 kcal, Portionsgröße",
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier      // hier kannst du .width(379.dp) etc. anhängen
            .height(52.dp)
            .width(379.dp),      // oder .fillMaxWidth()
        shape = RoundedCornerShape(8.dp),       // Corner-Radius
        color = Color(0xFF1F1F1F),              // Hintergrund
        tonalElevation = 0.dp,                  // kein extra Schatten (optional)
        onClick = onClick                       // Surface ist direkt klickbar
    ) {

        /* ----- eigentlicher Inhalt ----- */
        Row(
            modifier = Modifier
                .fillMaxSize()                  // füllt die ganze Surface
                .padding(8.dp),                 // Padding 8 (innen)
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)  // Gap 5
        ) {

            /* Textblock füllt Restbreite */
            Row(
                modifier = Modifier.weight(1f),
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

            /* Plus-Icon */
            IconButton(
                onClick = onClick,
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
        }
    }
}

@Preview
@Composable
fun GerichtButtonPreview() {
    GerichtButton(
        onClick = {}
    )
}