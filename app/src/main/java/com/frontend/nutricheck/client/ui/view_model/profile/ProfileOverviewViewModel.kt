package com.frontend.nutricheck.client.ui.view_model.profile

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.UserData
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

data class ProfileOverviewState(
    val userData: UserData = UserData(),
    val weightHistory: List<Int> = emptyList(),
    val selectedLanguage: String = "German",
    val selectedTheme: String = "Dark",
    val errorMessage: Int? = null
)

sealed interface ProfileOverviewEvent {
    object DisplayProfileOverview : ProfileOverviewEvent
    object DisplayPersonalData : ProfileOverviewEvent
    object DisplayWeightHistory : ProfileOverviewEvent
    object SelectLanguage : ProfileOverviewEvent
    data class UpdateUserData(val userData: UserData) : ProfileOverviewEvent
    data class SaveLanguage(val language: String) : ProfileOverviewEvent
    data class ChangeTheme(val theme: String) : ProfileOverviewEvent
}

@HiltViewModel
class ProfileOverviewViewModel @Inject constructor() :
    BaseProfileOverviewViewModel<ProfileOverviewState>(initialData = ProfileOverviewState()) {

    private val _events = MutableSharedFlow<ProfileOverviewEvent>()
    val events: SharedFlow<ProfileOverviewEvent> = _events.asSharedFlow()

    private val _data = MutableStateFlow(ProfileOverviewState())
    override val data: StateFlow<ProfileOverviewState> = _data.asStateFlow()

    fun onEvent(event: ProfileOverviewEvent) {
        when(event) {
            is ProfileOverviewEvent.DisplayProfileOverview -> { displayProfileOverview() }
            is ProfileOverviewEvent.DisplayPersonalData -> { displayPersonalData() }
            is ProfileOverviewEvent.DisplayWeightHistory -> { displayWeightHistory() }
            is ProfileOverviewEvent.SelectLanguage -> { editLanguage() }
            is ProfileOverviewEvent.SaveLanguage -> { onSaveLanguageClick(event.language) }
            is ProfileOverviewEvent.ChangeTheme -> { onChangeThemeClick(event.theme) }
            is ProfileOverviewEvent.UpdateUserData -> { validate(event.userData) }
        }
    }


    override fun onPersonalDataClick() {
        TODO("Not yet implemented")
    }

    override fun onWeightHistoryClick() {
        viewModelScope.launch {
            _events.emit(ProfileOverviewEvent.DisplayWeightHistory)
        }
        TODO("Get weight history from data source")
    }
    override fun editLanguage() {
        viewModelScope.launch {
            _events.emit(ProfileOverviewEvent.SelectLanguage)
        }
    }

    override fun onSaveLanguageClick(language: String) {
        TODO("persist language in userData repository")
    }
    override fun onChangeThemeClick(theme : String) {
        TODO("Not yet implemented")
    }
    override fun displayWeightHistory() {
        // Implementation for displaying weight history
    }
    override fun displayPersonalData() {
        // Implementation for displaying personal data
    }
    private fun displayProfileOverview() {
        viewModelScope.launch {
            _events.emit(ProfileOverviewEvent.DisplayProfileOverview)
        }
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
        viewModelScope.launch {
            _events.emit(ProfileOverviewEvent.DisplayProfileOverview)
        }
    }
    private fun persistData(userData: UserData) {
        TODO("Persist user data to the data source")
    }
    private fun isBirthdateInvalid(birthdate: Date): Boolean {
        val localBirthdate = Instant.ofEpochMilli(birthdate.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val today = LocalDate.now()
        val hundredYearsAgo = today.minusYears(100)
        return localBirthdate.isAfter(today) || localBirthdate.isBefore(hundredYearsAgo)

    }
}