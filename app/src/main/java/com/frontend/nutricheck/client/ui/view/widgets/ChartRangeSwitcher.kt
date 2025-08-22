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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ChartRangeSwitcher is a composable that allows users to switch
 * between different time periods for chart data.
 */
@Composable
fun ChartRangeSwitcher(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedOption: Int = 0,
    onSelect: (Int) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    val outerBg = colors.surfaceContainerLow
    val selectedBg = colors.secondary
    val unselectedBg = colors.surface
    val selectedTextColor = colors.onSecondary
    val unselectedTextColor = colors.outline

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
                val selected = index == selectedOption
                val bgColor by animateColorAsState(if (selected) selectedBg else Color.Transparent)
                val textColor by animateColorAsState(if (selected) selectedTextColor else
                    unselectedTextColor)

                Box(
                    modifier = Modifier
                        .width(buttonWidth)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(7.dp))
                        .background(bgColor)
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
