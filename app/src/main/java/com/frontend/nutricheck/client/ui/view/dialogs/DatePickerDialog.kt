package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DatePickerDialog(
    modifier: Modifier = Modifier,
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    initialYear: Int,
    initialMonth: Int,
    initialDayOfMonth: Int,
    onDismissRequest: () -> Unit = {}
) {}