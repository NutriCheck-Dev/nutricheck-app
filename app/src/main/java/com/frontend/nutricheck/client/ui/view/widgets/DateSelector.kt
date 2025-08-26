package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * A date selector bar that allows users to navigate through dates and open a calendar picker.
 */
@Composable
fun DateSelectorBar(
    selectedDate: Date,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onOpenCalendar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val todayFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val current = todayFormat.format(Date())
    val selected = todayFormat.format(selectedDate)
    val styles = MaterialTheme.typography

    val displayText = if (current == selected) "Today" else dateFormat.format(selectedDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous day",
                tint = Color(0xFF71727A))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = displayText,
            style = styles.labelMedium,
            color = Color(0xFF71727A),
            fontSize = 18.sp,
            modifier = Modifier.clickable { onOpenCalendar() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onNextDay) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next day",
                tint = Color(0xFF71727A))
        }
    }
}

@Preview
@Composable
fun DisplayDateSelectorBarPreview() {
    var showDatePicker by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.time)

    DateSelectorBar(
        selectedDate = selectedDate,
        onPreviousDay = { /* ... */ },
        onNextDay = { /* ... */ },
        onOpenCalendar = {
            showDatePicker = true
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Date(it)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Abbrechen") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}