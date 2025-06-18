package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsHeader(
    name: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1F1F1F)
    ) {
        Text(
            text = "Hi, $name",
            style = MaterialTheme.typography.headlineLarge.copy( // z. B. H5-H4
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    top = 42.dp,
                    bottom = 42.dp
                )
        )
    }
}

@Preview
@Composable
fun SettingsHeaderPreview() {
    SettingsHeader(
        name = "Moritz",
        modifier = Modifier.padding(16.dp)
    )
}
