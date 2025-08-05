package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

@Composable
fun ServingsPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorSize by remember { mutableStateOf(IntSize.Zero) }
    var anchorPosition by remember { mutableStateOf(Offset.Zero) }
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    val list = range.map { it.toString() }
    val visibleCount = 8
    val halfCount = (visibleCount - 1) / 2

    val initialIndex = list.indexOf(value.toString())
        .coerceAtLeast(0)

    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )
    val fling = rememberSnapFlingBehavior(lazyListState = state)

    Box(modifier = Modifier
        .width(180.dp)
        .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
        .border(1.dp, colors.outline, RoundedCornerShape(8.dp))
        .onGloballyPositioned { coordinates ->
            anchorPosition = coordinates.localToWindow(Offset.Zero)
            anchorSize = coordinates.size
        },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$value",
                color = colors.onSurfaceVariant,
                style = styles.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (!expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                tint = colors.onSurfaceVariant,
                contentDescription = null,
                )
            }
        }

        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false },
                offset = IntOffset(
                    x = anchorPosition.x.roundToInt(),
                    y= (anchorPosition.y + anchorSize.height).roundToInt()
                )
            ) {
                CustomNumberPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    list = list,
                    textStyle = styles.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    state = state,
                    flingBehavior = fling,
                    visibleCount = visibleCount,
                    halfCount = halfCount,
                    onSelectedIndexChange = { realIndex ->
                        onValueChange(range.elementAt(realIndex))
                    }
                )
            }
        }

}