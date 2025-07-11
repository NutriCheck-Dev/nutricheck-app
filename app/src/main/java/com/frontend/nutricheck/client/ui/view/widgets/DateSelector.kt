package com.frontend.nutricheck.client.ui.view.widgets

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DateSelector(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    OutlinedButton(
        modifier = modifier,
        onClick = { showDialog = true }
    ) {
        Text("Datum wählen: ${selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}")
    }

    if (showDialog) {
        val listener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            val date = LocalDate.of(year, month + 1, day)
            onDateSelected(date)
            showDialog = false
        }

        val calendar = Calendar.getInstance()
        calendar.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)

        DatePickerDialog(
            context,
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}