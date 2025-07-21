package com.frontend.nutricheck.client.ui.view_model.profile

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

data class ProfileState(
    val userData: UserData = UserData(),
    val weightData: List<Int> = emptyList(),
    val errorMessage: Int? = null
)

sealed interface ProfileEvent {
    object DisplayProfileOverview : ProfileEvent
    object DisplayPersonalData : ProfileEvent
    object DisplayWeightHistory : ProfileEvent
    object SelectLanguage : ProfileEvent
    object AddNewWeight : ProfileEvent
    data class UpdateUserData(val userData: UserData) : ProfileEvent
    data class SaveLanguage(val language: String) : ProfileEvent
    data class ChangeTheme(val theme: String) : ProfileEvent
    data class SaveNewWeight(val weight: String, val date: Date) : ProfileEvent
}

@HiltViewModel
class ProfileOverviewViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) :
    BaseProfileViewModel<ProfileState>(initialData = ProfileState()) {

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    private val _data = MutableStateFlow(ProfileState())
    override val data: StateFlow<ProfileState> = _data.asStateFlow()


    fun onEvent(event: ProfileEvent) {
        when(event) {
            is ProfileEvent.DisplayProfileOverview -> { displayProfileOverview() }
            is ProfileEvent.DisplayPersonalData -> { emitEvent(ProfileEvent.DisplayPersonalData) }
            is ProfileEvent.DisplayWeightHistory -> { displayWeightHistory() }
            is ProfileEvent.SelectLanguage -> { emitEvent(ProfileEvent.SelectLanguage) }
            is ProfileEvent.SaveLanguage -> { onSaveLanguageClick(event.language) }
            is ProfileEvent.ChangeTheme -> { onChangeThemeClick(event.theme) }
            is ProfileEvent.UpdateUserData -> { validate(event.userData) }
            is ProfileEvent.AddNewWeight -> { emitEvent(ProfileEvent.AddNewWeight) }
            is ProfileEvent.SaveNewWeight -> {saveNewWeight(event.weight, event.date)}
        }
    }

    private fun displayWeightHistory() {
//        viewModelScope.launch {
//            val weightHistory = userDataRepository.getWeightHistory()
//        }
        emitEvent(ProfileEvent.DisplayWeightHistory)

        TODO("Get weight history from data source")
    }
    private fun saveNewWeight(weight: String, date: Date) {
        val weightValue = weight.toIntOrNull()
        if (weightValue == null || weightValue <= 0) {
            _data.value = _data.value.copy(errorMessage = R.string.onboarding_error_weight_required)
            return
        }
        TODO("persist weight data")
    }
    private fun displayProfileOverview() {
        emitEvent(ProfileEvent.DisplayProfileOverview)
    }
    private fun onSaveLanguageClick(language: String) {
        val newUserData = _data.value.userData.copy(language = language)
        _data.value = _data.value.copy(userData = newUserData)
        persistData(newUserData)
    }
    private fun onChangeThemeClick(theme : String) {
        val newUserData = _data.value.userData.copy(theme = theme)
        _data.value = _data.value.copy(userData = newUserData)
        persistData(newUserData)
    }
    private fun validate(userData: UserData) {
        if (userData.username.isBlank()) {
            _data.value =
                _data.value.copy(errorMessage = R.string.onboarding_error_name_required)
            return
        } else if (isBirthdateInvalid(userData.birthdate)) {
            _data.value =
                _data.value.copy(errorMessage = R.string.onboarding_error_birthdate_required)
            return
        } else if (userData.height == null || userData.height <= 0) {
            _data.value =
                _data.value.copy(errorMessage = R.string.onboarding_error_height_required)
            return
        } else if (userData.weight == null || userData.weight <= 0) {
            _data.value =
                _data.value.copy(errorMessage = R.string.onboarding_error_weight_required)
            return
        } else if (userData.targetWeight == null || userData.targetWeight <= 0) {
            _data.value =
                _data.value.copy(errorMessage = R.string.onboarding_error_target_weight_required)
            return
        }
        _data.value = _data.value.copy(errorMessage = null, userData = userData)
        persistData(userData)
        displayProfileOverview()
    }
    private fun persistData(userData: UserData) {
        viewModelScope.launch {
            userDataRepository.updateUserData(userData)
        }
    }
    private fun isBirthdateInvalid(birthdate: Date): Boolean {
        val localBirthdate = Instant.ofEpochMilli(birthdate.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val today = LocalDate.now()
        val hundredYearsAgo = today.minusYears(100)
        return localBirthdate.isAfter(today) || localBirthdate.isBefore(hundredYearsAgo)
    }
    private fun emitEvent(event: ProfileEvent) = viewModelScope.launch { _events.emit(event) }

}