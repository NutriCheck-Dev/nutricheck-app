package com.frontend.nutricheck.client.ui.view.widgets

import android.content.res.Resources
import android.widget.EditText
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import com.frontend.nutricheck.client.ui.theme.AppTheme
import androidx.compose.ui.graphics.toArgb

@Composable
fun CustomNumberPicker(
    modifier: Modifier = Modifier,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    Box(modifier = Modifier
        .width(180.dp)
        .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
        .border(1.dp, colors.outline, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            color = colors.surfaceVariant,
            modifier = Modifier
                .wrapContentSize()
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$value",
                    style = styles.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
            }
        }
        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false },
                alignment = Alignment.TopStart,
                offset = IntOffset(0, 48)
            ) {
                Surface(
                    tonalElevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(160.dp)
                        .background(colors.surface)
                ) {
                    AndroidView(
                        modifier = modifier.fillMaxSize(),
                        factory =  { context ->
                            NumberPicker(context).apply {
                                minValue = range.first
                                maxValue = range.last
                                wrapSelectorWheel = true
                                post {
                                    val input = findViewById<EditText>(
                                        Resources.getSystem().getIdentifier(
                                            "numberpicker_input",
                                            "id",
                                            "android"
                                        )
                                    )
                                    input?.setTextColor(colors.onSurfaceVariant.toArgb())
                                    input?.highlightColor = colors.onSurfaceVariant.toArgb()
                                }
                                setOnValueChangedListener { _, _, new ->
                                    onValueChange(new)
                                }
                            }
                        },
                        update = { picker ->
                            if (picker.value != value) picker.value = value
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AndroidNumberPickerPreview() {
    var value by remember { mutableIntStateOf(5) }
    AppTheme(darkTheme = true) {
        CustomNumberPicker(
            value = value,
            range = 0..10,
            onValueChange = { newValue -> value = newValue }
        )
    }
}