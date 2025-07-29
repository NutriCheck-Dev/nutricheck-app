package com.frontend.nutricheck.client.ui.view_model.profile

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.AppThemeState
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.Language
import com.frontend.nutricheck.client.model.data_sources.data.ThemeSetting
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

sealed interface ProfileEvent {
    object DisplayProfileOverview : ProfileEvent
    object DisplayPersonalData : ProfileEvent
    object DisplayWeightHistory : ProfileEvent
    object SelectLanguage : ProfileEvent
    object AddNewWeight : ProfileEvent
    object OnSaveClick : ProfileEvent
    object RestartApp : ProfileEvent
    data class SaveLanguage(val language: Language) : ProfileEvent
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



@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val appSettingsRepository: AppSettingsRepository
) : BaseProfileViewModel() {

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()
    val initialBirthdate = Date(1234567890L) // 1.1.1970

    private val _dataDraft = MutableStateFlow<UserData>(UserData(
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
    ))
    val dataDraft: StateFlow<UserData> = _dataDraft.asStateFlow()

    private val _data = MutableStateFlow<UserData>(UserData(
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
    ))
    val data: StateFlow<UserData> = _data.asStateFlow()

    private val _weightData = MutableStateFlow<List<Weight>>(emptyList())
    val weightData: StateFlow<List<Weight>> = _weightData.asStateFlow()

    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorMessage: StateFlow<Int?> = _errorMessage.asStateFlow()

    val currentLanguage: StateFlow<Language> = appSettingsRepository.language.stateIn(
        viewModelScope,
        initialValue = Language.GERMAN,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000)
    )

    fun onEvent(event: ProfileEvent) {
        when(event) {
            is ProfileEvent.DisplayProfileOverview -> { displayProfileOverview() }
            is ProfileEvent.DisplayPersonalData -> { emitEvent(ProfileEvent.DisplayPersonalData) }
            is ProfileEvent.DisplayWeightHistory -> { displayWeightHistory() }
            is ProfileEvent.SelectLanguage -> { emitEvent(ProfileEvent.SelectLanguage) }
            is ProfileEvent.SaveLanguage -> { onSaveLanguageClick(event.language) }
            is ProfileEvent.ChangeTheme -> { onChangeThemeClick(event.theme) }
            is ProfileEvent.OnSaveClick -> { persistDataWithCalculation() }
            is ProfileEvent.AddNewWeight -> { emitEvent(ProfileEvent.AddNewWeight) }
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
            is ProfileEvent.RestartApp -> { emitEvent(ProfileEvent.RestartApp) }
        }
    }
    init {
        viewModelScope.launch {
            _data.value = userDataRepository.getUserData()
            _dataDraft.value = _data.value
        }
    }

    private fun updateUserNameDraft(username: String) {
        if (username.isBlank()) {
            _errorMessage.value = R.string.userData_error_name_required
            return
        }
        _errorMessage.value = null
        _dataDraft.value = _dataDraft.value.copy(username = username)
    }
    private fun updateUserBirthdateDraft(birthdate: Date) {
        if (Utils.isBirthdateInvalid(birthdate)) {
            _errorMessage.value = R.string.userData_error_birthdate_required
            return
        }
        _errorMessage.value = null
        _dataDraft.value = _dataDraft.value.copy(birthdate = birthdate)
    }
    private fun updateUserHeightDraft(height: String) {
        val heightValue = height.toDoubleOrNull()
        if (heightValue == null || heightValue <= 0.0) {
            _errorMessage.value = R.string.userData_error_height_required
            return
        }
        _errorMessage.value = null
        _dataDraft.value = _dataDraft.value.copy(height = heightValue)
    }
    private fun updateUserWeightDraft(weight: String) {
        val weightValue = weight.toDoubleOrNull()
        if (weightValue == null || weightValue <= 0.0) {
            _errorMessage.value = R.string.userData_error_weight_required
            return
        }
        _errorMessage.value = null
        _dataDraft.value = _dataDraft.value.copy(weight = weightValue)
    }
    private fun updateUserTargetWeightDraft(targetWeight: String) {
        val targetWeightValue = targetWeight.toDoubleOrNull()
        if (targetWeightValue == null || targetWeightValue <= 0.0) {
            _errorMessage.value = R.string.userData_error_target_weight_required
            return
        }
        _errorMessage.value = null
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
            _errorMessage.value = R.string.userData_error_weight_required
            return
        }
        viewModelScope.launch {
            userDataRepository.addWeight(Weight(value = weightValue.toDouble(), enterDate = date))
        }
    }
    private fun displayProfileOverview() {
        emitEvent(ProfileEvent.DisplayProfileOverview)
    }
    private fun onSaveLanguageClick(language: Language) {
        viewModelScope.launch {
            appSettingsRepository.setLanguage(language)
        }
        emitEvent(ProfileEvent.RestartApp)
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
        val userDataWithCalories = Utils.calculateNutrition(userData = UserData(
            username = _dataDraft.value.username,
            birthdate = _dataDraft.value.birthdate,
            gender = _dataDraft.value.gender,
            height = _dataDraft.value.height,
            weight = _dataDraft.value.weight,
            targetWeight = _dataDraft.value.targetWeight,
            activityLevel = _dataDraft.value.activityLevel,
            weightGoal = _dataDraft.value.weightGoal,
            age = Utils.calculateAge(_dataDraft.value.birthdate),
            dailyCaloriesGoal = _dataDraft.value.dailyCaloriesGoal,
            proteinGoal = _dataDraft.value.proteinGoal,
            carbsGoal = _dataDraft.value.carbsGoal,
            fatsGoal = _dataDraft.value.fatsGoal
        ))
        viewModelScope.launch {
            userDataRepository.updateUserData(userDataWithCalories)
        }
        displayProfileOverview()
    }
    private fun emitEvent(event: ProfileEvent) = viewModelScope.launch { _events.emit(event) }




}