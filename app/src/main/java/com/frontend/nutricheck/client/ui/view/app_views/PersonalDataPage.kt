package com.frontend.nutricheck.client.ui.view.app_views

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileEvent
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileState
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PersonalDataPage(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    onBack: () -> Unit = {}

) {
    var editableUserData by remember { mutableStateOf(state.userData) }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(state.userData) {
        editableUserData = state.userData
    }
    ViewsTopBar(
        navigationIcon = { NavigateBackButton(onBack = onBack) },
        title = { Text(stringResource(id = R.string.profile_menu_item_personal_data)) }
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
            userData = editableUserData,
            onUserDataChange = { updatedUserData ->
                editableUserData = updatedUserData},
            onBirthdateClick = {
                showDatePicker = true
            }
        )
        item {
            state.errorMessage?.let { errorResId ->
                Text(
                    text = stringResource(id = errorResId),
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
                    onEvent(ProfileEvent.UpdateUserData(editableUserData))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.save))
            }
        }
    }
    if (showDatePicker) {
        val calendar = Calendar.getInstance().apply {
            time = editableUserData.birthdate
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val newCal = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                editableUserData = editableUserData.copy(birthdate = newCal.time)
                showDatePicker = false
            }, year, month, day
        )
        datePickerDialog.setOnDismissListener {
            showDatePicker = false
        }
        datePickerDialog.show()
    }
}
private fun LazyListScope.personalDataFormItems(
    userData: UserData,
    onUserDataChange: (UserData) -> Unit,
    onBirthdateClick: () -> Unit
) {
    item {
        EditableDataRow(
            label = stringResource(id = R.string.profile_menu_item_name),
            value = userData.username,
            onValueChange = { onUserDataChange(userData.copy(username = it)) },
            keyboardType = KeyboardType.Text
        )
    }
    item {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        Row(modifier = Modifier.clickable { onBirthdateClick() }) {
            EditableDataRow(
                label = stringResource(id = R.string.onboarding_label_birthdate),
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
                selectedGender?.let {
                    onUserDataChange(userData.copy(gender = it))
                }
            }
        )
    }
    item {
        EditableDataRow(
            label = stringResource(id = R.string.profile_menu_height),
            value = userData.height?.toString() ?: "",
            onValueChange = { onUserDataChange(userData.copy(height = it.toDoubleOrNull())) },
            keyboardType = KeyboardType.Number
        )
    }
    item {
        EditableDataRow(
            label = stringResource(id = R.string.profile_menu_weight),
            value = userData.weight?.toString() ?: "",
            onValueChange = { onUserDataChange(userData.copy(weight = it.toDoubleOrNull())) },
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
                selectedGoal?.let {
                    onUserDataChange(userData.copy(weightGoal = it))
                }
            }
        )
    }
    item {
        EditableDataRow(
            label = stringResource(id = R.string.onboarding_label_target_weight),
            value = userData.targetWeight?.toString() ?: "",
            onValueChange = { onUserDataChange(userData.copy(targetWeight = it.toDoubleOrNull())) },
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
                selectedLevel?.let {
                    onUserDataChange(userData.copy(activityLevel = it))
                }
            }
        )
    }
}
@Composable
private fun formatGender(gender: Gender): String {
    return when (gender) {
        Gender.MALE -> stringResource(id = R.string.onboarding_label_gender_male)
        Gender.FEMALE -> stringResource(id = R.string.onboarding_label_gender_female)
        Gender.DIVERS -> stringResource(id = R.string.onboarding_label_gender_diverse)
    }
}

@Composable
private fun formatActivityLevel(level: ActivityLevel): String {
    return when (level) {
        ActivityLevel.OCCASIONALLY ->
            stringResource(id = R.string.onboarding_label_activity_level_occasionally)
        ActivityLevel.REGULARLY ->
            stringResource(id = R.string.onboarding_label_activity_level_regularly)
        ActivityLevel.NEVER ->
            stringResource(id = R.string.onboarding_label_activity_level_never)
        ActivityLevel.FREQUENTLY ->
            stringResource(id = R.string.onboarding_label_activity_level_frequently)
    }
}
@Composable
private fun formatWeightGoal(goal: WeightGoal): String {
    return when (goal) {
        WeightGoal.GAIN_WEIGHT -> stringResource(id = R.string.onboarding_label_goal_gain_weight)
        WeightGoal.LOSE_WEIGHT -> stringResource(id = R.string.onboarding_label_goal_lose_weight)
        WeightGoal.MAINTAIN_WEIGHT ->
            stringResource(id = R.string.onboarding_label_goal_maintain_weight)
    }
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
            modifier = Modifier.weight(1f)
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

@Preview (showBackground = true)
@Composable
fun PersonalDataPreview() {
    val previewDate = Calendar.getInstance().apply {
        set(2000, Calendar.JUNE, 15)
    }.time
    val previewUserData = UserData(
        username = "Max Mustermann",
        birthdate = previewDate,
        gender = Gender.MALE,
        height = 180.0,
        weight = 75.0,
        weightGoal = WeightGoal.MAINTAIN_WEIGHT,
        targetWeight = 75.0,
        activityLevel = ActivityLevel.OCCASIONALLY
    )

    PersonalDataPage(
        state = ProfileState(userData = previewUserData),
        onEvent = {},
        onBack = {}
    )
}