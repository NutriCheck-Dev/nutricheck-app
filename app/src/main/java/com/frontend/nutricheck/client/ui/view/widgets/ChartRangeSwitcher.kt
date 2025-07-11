package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ChartRangeSwitcher is a composable that allows users to switch between different time periods for chart data.
@Composable
fun ChartRangeSwitcher(
    modifier: Modifier = Modifier,
    options: List<String> = listOf("7T", "30T", "90T"),
    selectedOption: Int = 0,
    onSelect: (String) -> Unit = {}
) {
    val outerBg = Color(0xFF000000)
    val selectedBg = Color.White
    val unselectedBg = Color(0xFFBBBBBB)
    val selectedTextColor = Color(0xFF121212)
    val unselectedTextColor = Color(0xFF707179)

    val totalWidth = 105.dp
    val totalHeight = 18.dp
    val buttonWidth = totalWidth / options.size

    Box(
        modifier = modifier
            .width(totalWidth)
            .height(totalHeight)
            .clip(RoundedCornerShape(9.dp))
            .background(outerBg)
            .padding(1.dp) // dünner äußerer Rand
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(9.dp))
                .background(unselectedBg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, label ->
                val isSelected = index == selectedOption
                val bgColor by animateColorAsState(if (isSelected) selectedBg else Color.Transparent)
                val textColor by animateColorAsState(if (isSelected) selectedTextColor else unselectedTextColor)

                Box(
                    modifier = Modifier
                        .width(buttonWidth)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(7.dp))
                        .background(bgColor)
                        .clickable { onSelect(label) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChartRangePreview() {
    var selected by remember { mutableStateOf(1) }

    ChartRangeSwitcher(
        options = listOf("7T", "30T", "90T"),
        selectedOption = selected,
        onSelect = { clicked ->
            selected = listOf("7T", "30T", "90T").indexOf(clicked)
        }
    )
}