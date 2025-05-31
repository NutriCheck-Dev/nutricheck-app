package com.frontend.nutricheck.util

import android.widget.Switch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ButtonListe(
    modifier: Modifier,
    items: List<@Composable () -> Unit> // Liste von Composable-Funktionen für die Einstellungen
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), shape = RoundedCornerShape(16.dp))
            .padding(vertical = 8.dp)
    ) {
        items.forEachIndexed { index, item ->
            item()

            // Divider zwischen Items (außer nach dem letzten)
            if (index != items.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(start = 64.dp, end = 16.dp)  // damit nicht bis ganz links/rechts
                        .height(1.dp),
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun EinstellungsBlock() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        ButtonListe(
            modifier = Modifier.fillMaxWidth(),
            items = listOf(
                {
                    EinstellungButton(
                        icon = Icons.Default.AccountCircle,
                        title = "Persönliche Daten & Ziel",
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { /* Navigate */ }
                    )
                },
                {
                    EinstellungButton(
                        icon = Icons.Default.DateRange,
                        title = "Gewichtsverlauf",
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { /* Navigate */ }
                    )
                },
                {
                    EinstellungButton(
                        icon = Icons.Default.Build,
                        title = "Optik",
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        }
                    )
                },
                {
                    EinstellungButton(
                        icon = Icons.Default.Star,
                        title = "Sprache",
                        trailingContent = {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        },
                        onClick = { /* Navigate */ }
                    )
                }
            )
        )
    }
}

@Preview
@Composable
fun EinstellungsBlockPreview() {
    EinstellungsBlock()
}
