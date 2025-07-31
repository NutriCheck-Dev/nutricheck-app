package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileEvent
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PersonalDataPage(
    state: UserData,
    errorState : BaseViewModel.UiState,
    onEvent: (ProfileEvent) -> Unit,
    onBack: () -> Unit = {}

) {
    var selectedDate by remember { mutableStateOf(state.birthdate) }
    var showDatePicker by remember { mutableStateOf(false) }

        ViewsTopBar(
            navigationIcon = { NavigateBackButton(onBack = { onBack() }) },
            title = { Text(stringResource(id = R.string.profile_menu_item_personal_data),) }
        )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
        personalDataFormItems(
            userData = state,
            onEvent = onEvent,
            onBirthdateClick = {
                showDatePicker = true
            }
        )
        item {
            if (errorState is BaseViewModel.UiState.Error) {
                Text(
                    text = errorState.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onEvent(ProfileEvent.OnSaveClick)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.save))
            }
        }
    }
    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = selectedDate.time)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Date(it)
                        }
                        onEvent(ProfileEvent.UpdateUserBirthdateDraft(selectedDate))
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
}


private fun LazyListScope.personalDataFormItems(
    userData: UserData,
    onEvent: (ProfileEvent) -> Unit,
    onBirthdateClick: () -> Unit
) {
    item {
        EditableDataRow(
            label = stringResource(id = R.string.userData_label_name),
            value = userData.username,
            onValueChange = { onEvent(ProfileEvent.UpdateUserNameDraft(it)) },
            keyboardType = KeyboardType.Text
        )
    }
    item {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        Row(modifier = Modifier.clickable { onBirthdateClick() }) {
            EditableDataRow(
                label = stringResource(id = R.string.userData_label_birthdate),
                value = dateFormat.format(userData.birthdate),
                onValueChange = { /* Bleibt leer wegen DatePicker */ },
                keyboardType = KeyboardType.Text,
                readOnly = true
            )
        }

    }
    item {
        val genderOptions = Gender.entries.map { gender ->
            Pair(gender, formatGender(gender))
        }

        EditableDropdownRow(
            label = stringResource(id = R.string.profile_menu_item_gender),
            selectedValue = formatGender(userData.gender),
            options = genderOptions.map { it.second },
            onOptionSelected = { formattedString ->
                val selectedGender = genderOptions.find { it.second == formattedString }?.first
                selectedGender?.let { onEvent(ProfileEvent.UpdateUserGenderDraft(it))
                }
            }
        )
    }
    item {
        EditableDataRow(
            label = stringResource(id = R.string.profile_menu_height),
            value = userData.height.toString(),
            onValueChange = { onEvent(ProfileEvent.UpdateUserHeightDraft(it)) },
            keyboardType = KeyboardType.Number
        )
    }
    item {
        EditableDataRow(
            label = stringResource(id = R.string.profile_menu_weight),
            value = userData.weight.toString(),
            onValueChange = { onEvent(ProfileEvent.UpdateUserWeightDraft(it)) },
            keyboardType = KeyboardType.Number
        )
    }
    item {
        val weightGoalOptions = WeightGoal.entries.map { goal ->
            Pair(goal, formatWeightGoal(goal))
        }
        EditableDropdownRow(
            label = stringResource(id = R.string.profile_menu_item_goal),
            selectedValue = formatWeightGoal(userData.weightGoal),
            options = WeightGoal.entries.map { formatWeightGoal(it) },
            onOptionSelected = { formattedString ->
                val selectedGoal = weightGoalOptions.find { it.second == formattedString }?.first
                selectedGoal?.let { onEvent(ProfileEvent.UpdateUserWeightGoalDraft(it)) }
            }
        )
    }
    item {
        EditableDataRow(
            label = stringResource(id = R.string.userData_label_target_weight),
            value = userData.targetWeight.toString(),
            onValueChange = { onEvent(ProfileEvent.UpdateUserTargetWeightDraft(it)) },
            keyboardType = KeyboardType.Number
        )
    }
    item {
        val activityLevelOptions = ActivityLevel.entries.map { level ->
        Pair(level, formatActivityLevel(level))
        }
        EditableDropdownRow(
            label = stringResource(id = R.string.profile_menu_item_activity_level),
            selectedValue = formatActivityLevel(userData.activityLevel),
            options = activityLevelOptions.map { it.second },
            onOptionSelected = { formattedString ->
                val selectedLevel = activityLevelOptions.find { it.second == formattedString }?.first
                selectedLevel?.let { onEvent(ProfileEvent.UpdateUserActivityLevelDraft(it)) }
            }
        )
    }
}
@Composable
private fun formatGender(gender: Gender): String {
    return gender.getDescription(LocalContext.current)
}

@Composable
private fun formatActivityLevel(level: ActivityLevel): String {
    return level.getDescription(LocalContext.current)
}
@Composable
private fun formatWeightGoal(goal: WeightGoal): String {
    return goal.getDescription(LocalContext.current)
}

@Composable
private fun EditableDataRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EditableDropdownRow(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            TextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

