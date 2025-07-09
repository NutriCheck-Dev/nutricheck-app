package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


// ChartRangeSwitcher is a composable that allows users to switch between different time periods for chart data.
@Composable
fun ChartRangeSwitcher(
    modifier: Modifier = Modifier,
    firstPeriod: String = "1M",
    secondPeriod: String = "6M",
    thirdPeriod: String = "12M",
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit = {}
) {
    val options = listOf(firstPeriod, secondPeriod, thirdPeriod)

    Row(
        //horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .wrapContentWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF2A2A2A))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        options.forEach { label ->
            val isSelected = selectedPeriod == label
            PeriodButton(
                title = label,
                selected = isSelected,
                modifier = Modifier
                    .height(28.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPeriodSelected(label) }
            )
        }
    }
}


@Composable
fun PeriodButton(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) Color(0xFFF2F2F2) else Color(0xFFBBBBBB)
    val textColor = if (selected) Color(0xFF121212) else Color(0xFF666666)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight(700),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun ChartRangeSwitcherPreview() {
    var selected by remember { mutableStateOf("XD") }

    ChartRangeSwitcher(
        selectedPeriod = selected,
        onPeriodSelected = { selected = it }
    )
}