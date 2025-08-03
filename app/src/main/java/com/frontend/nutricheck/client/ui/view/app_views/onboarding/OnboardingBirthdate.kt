package com.frontend.nutricheck.client.ui.view.app_views.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.ui.view_model.OnboardingEvent
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.OnboardingState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OnboardingBirthdate(
    state : OnboardingState,
    onEvent : (OnboardingEvent) -> Unit,
    errorState : BaseViewModel.UiState
    ) {
    var selectedDate by remember { mutableStateOf(state.birthdate) }
    var showDatePicker by remember { mutableStateOf(true) }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val displayDate = selectedDate?.let { dateFormat.format(it) } ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .height(44.dp)
            ) {
                Image(
                    modifier = Modifier
                        .width(44.dp)
                        .height(44.dp),
                    painter = painterResource(id = R.drawable.nutri_check_apple_start),
                    contentDescription = "NutriCheck Logo",
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    text = stringResource(id = R.string.app_name),
                    style = TextStyle(
                        fontSize = 36.sp,
                        lineHeight = 44.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )
            }
            Text(
                modifier = Modifier.padding(top = 150.dp).padding(bottom = 16.dp),
                text = stringResource(id = R.string.onboarding_question_birthdate),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 36.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )
            )

            OutlinedTextField(
                value = displayDate,
                onValueChange = { },
                modifier = Modifier
                    .width(300.dp)
                    .clickable { showDatePicker = true },
                label = {
                    Text(stringResource(id = R.string.userData_label_birthdate))
                },
                readOnly = true,
                isError = errorState is BaseViewModel.UiState.Error,
                singleLine = true,
                textStyle = TextStyle( color = Color(0xFFFFFFFF))
            )
            if (errorState is BaseViewModel.UiState.Error) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = errorState.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

        }
        if (showDatePicker) {
            val datePickerState =
                rememberDatePickerState(initialSelectedDateMillis = selectedDate?.time)
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
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .height(40.dp)
                .width(286.dp),
            shape = RoundedCornerShape(size = 28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4580FF)
            ),
            enabled = selectedDate != null,
            onClick = {
                onEvent(OnboardingEvent.EnterBirthdate(selectedDate))})
        {
            Text(
                text = stringResource(id = R.string.onboarding_button_next),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}
