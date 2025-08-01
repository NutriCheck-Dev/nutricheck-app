package com.frontend.nutricheck.client.ui.view_model

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.AppThemeState
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.ThemeSetting
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
/**
 * Represents all possible events that can occur in the profile screen.
 */
sealed interface ProfileEvent {
    // Navigation events
    object NavigateToPersonalData : ProfileEvent
    object NavigateToAddNewWeight : ProfileEvent
    object NavigateToProfileOverview : ProfileEvent
    object RestartApp : ProfileEvent
    // collected events, which lead to navigation
    object OnPersonalDataClick : ProfileEvent
    object OnAddNewWeightClick : ProfileEvent
    object OnDisplayProfileOverview : ProfileEvent
    object OnRestartApp : ProfileEvent
    // collected events, which handle data changes
    object DisplayWeightHistory : ProfileEvent
    object OnSaveClick : ProfileEvent
    data class ChangeTheme(val theme: ThemeSetting) : ProfileEvent
    data class SaveNewWeight(val weight: String, val date: Date) : ProfileEvent
    data class UpdateUserNameDraft(val username: String) : ProfileEvent
    data class UpdateUserBirthdateDraft(val birthdate: Date) : ProfileEvent
    data class UpdateUserHeightDraft(val height: String) : ProfileEvent
    data class UpdateUserWeightDraft(val weight: String) : ProfileEvent
    data class UpdateUserTargetWeightDraft(val targetWeight: String) : ProfileEvent
    data class UpdateUserActivityLevelDraft(val activityLevel: ActivityLevel) : ProfileEvent
    data class UpdateUserWeightGoalDraft(val weightGoal: WeightGoal) : ProfileEvent
    data class UpdateUserGenderDraft(val gender : Gender) : ProfileEvent
}
/**
 * ViewModel for the user profile screen.
 * Manages user data, weight history and theme settings, and handles profile events.
 *
 * @property userDataRepository Repository for user data persistence.
 * @property appSettingsRepository Repository for app settings (theme).
 */

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val appSettingsRepository: AppSettingsRepository,
    @ApplicationContext private val appContext: Context
) : BaseViewModel() {
    private val defaultUserData = UserData(
        username = "",
        birthdate = Date(),
        gender = Gender.MALE,
        height = 0.0,
        weight = 0.0,
        targetWeight = 0.0,
        activityLevel = ActivityLevel.NEVER,
        weightGoal = WeightGoal.LOSE_WEIGHT,
        age = 0,
        dailyCaloriesGoal = 0,
        proteinGoal = 0,
        carbsGoal = 0,
        fatsGoal = 0
    )
    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    private val _dataDraft = MutableStateFlow<UserData>(defaultUserData)
    val dataDraft: StateFlow<UserData> = _dataDraft.asStateFlow()

    private val _data = MutableStateFlow<UserData>(defaultUserData)
    val data: StateFlow<UserData> = _data.asStateFlow()

    private val _weightData = MutableStateFlow<List<Weight>>(emptyList())
    val weightData: StateFlow<List<Weight>> = _weightData.asStateFlow()

    /**
     * Handles profile events and updates the state accordingly.
     * @param event The profile event to handle.
     */
    fun onEvent(event: ProfileEvent) {
        when(event) {
            //handled events by the ViewModel
            is ProfileEvent.DisplayWeightHistory -> { displayWeightHistory() }
            is ProfileEvent.OnSaveClick -> { persistDataWithCalculation() }
            is ProfileEvent.ChangeTheme -> { onChangeThemeClick(event.theme) }
            is ProfileEvent.SaveNewWeight -> {saveNewWeight(event.weight, event.date)}
            is ProfileEvent.UpdateUserNameDraft -> {updateUserNameDraft(event.username)}
            is ProfileEvent.UpdateUserBirthdateDraft -> {updateUserBirthdateDraft(event.birthdate)}
            is ProfileEvent.UpdateUserHeightDraft -> {updateUserHeightDraft(event.height)}
            is ProfileEvent.UpdateUserWeightDraft -> {updateUserWeightDraft(event.weight)}
            is ProfileEvent.UpdateUserTargetWeightDraft ->
                {updateUserTargetWeightDraft(event.targetWeight)}
            is ProfileEvent.UpdateUserActivityLevelDraft ->
                {updateUserActivityLevelDraft(event.activityLevel)}
            is ProfileEvent.UpdateUserWeightGoalDraft ->
                {updateUserWeightGoalDraft(event.weightGoal)}
            is ProfileEvent.UpdateUserGenderDraft -> {updateUserGenderDraft(event.gender)}
            //Ui Events, which are being handled by emitting a new event by the ViewModel
            is ProfileEvent.OnPersonalDataClick -> { emitEvent(ProfileEvent.NavigateToPersonalData) }
            is ProfileEvent.OnAddNewWeightClick -> { emitEvent(ProfileEvent.NavigateToAddNewWeight) }
            is ProfileEvent.OnDisplayProfileOverview ->
            { emitEvent(ProfileEvent.NavigateToProfileOverview) }
            is ProfileEvent.OnRestartApp -> { emitEvent(ProfileEvent.RestartApp) }
            else -> { /* No action needed for other navigation events */}

        }
    }
    init {
        viewModelScope.launch {
            _data.value = userDataRepository.getUserData()
            _dataDraft.value = _data.value
            _weightData.value = userDataRepository.getWeightHistory()
        }
    }

    private fun updateUserNameDraft(username: String) {
        if (username.isBlank()) {
            setError(appContext.getString(R.string.userData_error_name_required))
            return
        }
        setReady()
        _dataDraft.value = _dataDraft.value.copy(username = username)
    }
    private fun updateUserBirthdateDraft(birthdate: Date) {
        if (UserDataUtilsLogic.isBirthdateInvalid(birthdate)) {
            setError(appContext.getString(R.string.userData_error_birthdate_required))
            return
        }
        setReady()
        _dataDraft.value = _dataDraft.value.copy(birthdate = birthdate)
    }
    private fun updateUserHeightDraft(height: String) {
        val heightValue = height.toDoubleOrNull()
        if (heightValue == null || heightValue <= 0.0) {
            setError(appContext.getString(R.string.userData_error_height_required))
            return
        }
        setReady()
        _dataDraft.value = _dataDraft.value.copy(height = heightValue)
    }
    private fun updateUserWeightDraft(weight: String) {
        val weightValue = weight.toDoubleOrNull()
        if (weightValue == null || weightValue <= 0.0) {
            setError(appContext.getString(R.string.userData_error_weight_required))
            return
        }
        setReady()
        _dataDraft.value = _dataDraft.value.copy(weight = weightValue)
    }
    private fun updateUserTargetWeightDraft(targetWeight: String) {
        val targetWeightValue = targetWeight.toDoubleOrNull()
        if (targetWeightValue == null || targetWeightValue <= 0.0) {
            setError(appContext.getString(R.string.userData_error_target_weight_required))
            return
        }
        setReady()
        _dataDraft.value = _dataDraft.value.copy(targetWeight = targetWeightValue)
    }
    private fun updateUserActivityLevelDraft(activityLevel: ActivityLevel) {
        _dataDraft.value = _dataDraft.value.copy(activityLevel = activityLevel)
    }
    private fun updateUserWeightGoalDraft(weightGoal: WeightGoal) {
        _dataDraft.value = _dataDraft.value.copy(weightGoal = weightGoal)
    }
    private fun updateUserGenderDraft(gender : Gender) {
        _dataDraft.value = _dataDraft.value.copy(gender = gender)
    }
    private fun displayWeightHistory() {
        viewModelScope.launch {
            _weightData.value = userDataRepository.getWeightHistory()
        }
        emitEvent(ProfileEvent.DisplayWeightHistory)
    }
    private fun saveNewWeight(weight: String, date: Date) {
        val weightValue = weight.toDoubleOrNull()
        if (weightValue == null || weightValue <= 0) {
            setError(appContext.getString(R.string.userData_error_weight_required))
            return
        }
        if (date > Date() || date < data.value.birthdate) {
            setError(appContext.getString(R.string.userData_error_invalid_date))

            return
        }
        setReady()
        viewModelScope.launch {
            userDataRepository.addWeight(Weight(value = weightValue.toDouble(), date = date))
            _weightData.value = userDataRepository.getWeightHistory()
        }
    }

    private fun onChangeThemeClick(theme : ThemeSetting) {
        AppThemeState.currentTheme.value = theme
        val isDarkMode = when (theme) {
            ThemeSetting.DARK -> true
            ThemeSetting.LIGHT -> false
        }
        viewModelScope.launch {
            appSettingsRepository.setTheme(isDarkMode)
        }
    }
    private fun persistDataWithCalculation () {
        val userDataWithCalories = UserDataUtilsLogic.calculateNutrition(userData = UserData(
            username = _dataDraft.value.username,
            birthdate = _dataDraft.value.birthdate,
            gender = _dataDraft.value.gender,
            height = _dataDraft.value.height,
            weight = _dataDraft.value.weight,
            targetWeight = _dataDraft.value.targetWeight,
            activityLevel = _dataDraft.value.activityLevel,
            weightGoal = _dataDraft.value.weightGoal,
            age = UserDataUtilsLogic.calculateAge(_dataDraft.value.birthdate),
            dailyCaloriesGoal = _dataDraft.value.dailyCaloriesGoal,
            proteinGoal = _dataDraft.value.proteinGoal,
            carbsGoal = _dataDraft.value.carbsGoal,
            fatsGoal = _dataDraft.value.fatsGoal
        ))
        viewModelScope.launch {
            userDataRepository.updateUserData(userDataWithCalories)
        }
        _data.value = userDataWithCalories
        emitEvent(ProfileEvent.NavigateToProfileOverview)
    }
    private fun emitEvent(event: ProfileEvent) = viewModelScope.launch { _events.emit(event) }




}