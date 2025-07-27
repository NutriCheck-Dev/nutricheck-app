package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ButtonList(
    modifier: Modifier,
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1F1F1F), shape = RoundedCornerShape(16.dp))
    ) {
        items.forEachIndexed { index, item ->
            item()

            if (index != items.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp),
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun SettingsBlock() {
    var checked by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Color(0xFF1F1F1F))
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        ButtonList(
            modifier = Modifier.fillMaxWidth(),
            items = listOf(
                {
                    SettingsBlockButton(
                        icon = Icons.Default.AccountCircle,
                        title = "Persönliche Daten & Ziel",
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        onClick = { /* Navigate */ }
                    )
                },
                {
                    SettingsBlockButton(
                        icon = Icons.Default.DateRange,
                        title = "Gewichtsverlauf",
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        onClick = { /* Navigate */ }
                    )
                },
                {
                    SettingsBlockButton(
                        icon = Icons.Default.Build,
                        title = "Optik",
                        trailingContent = {
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                colors = SwitchDefaults.colors()

                            )
                        }
                    )
                },
                {
                    SettingsBlockButton(
                        icon = Icons.Default.Star,
                        title = "Sprache",
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.White
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
fun SettingsBlockPreview() {
    SettingsBlock()
}
