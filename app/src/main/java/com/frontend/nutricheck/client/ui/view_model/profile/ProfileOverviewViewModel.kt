package com.frontend.nutricheck.client.ui.view_model.profile

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class ProfileOverviewState(
    val userData: UserData = UserData(),
    val weightHistory: List<Int> = emptyList(),
    val selectedLanguage: String = "German",
    val selectedTheme: String = "Dark"
)

sealed interface ProfileOverviewEvent {
    data class DisplayPersonalData(val userData: UserData) : ProfileOverviewEvent
    data class DisplayWeightHistory(val weightHistory: List<Int>) : ProfileOverviewEvent
    data class SelectLanguage(val language: String) : ProfileOverviewEvent
    data class SelectTheme(val theme: String) : ProfileOverviewEvent
}

@HiltViewModel
class ProfileOverviewViewModel @Inject constructor() : BaseProfileOverviewViewModel<UserData>(
    UserData()
) {

    private val _events = MutableSharedFlow<ProfileOverviewEvent>()
    val events: SharedFlow<ProfileOverviewEvent> = _events.asSharedFlow()


    fun onEvent(event: ProfileOverviewEvent) {}

    override fun onPersonalDataClick() {
        TODO("Not yet implemented")
    }

    override fun onWeightHistoryClick() {
        viewModelScope.launch {
            _events.emit(
                ProfileOverviewEvent
                    .DisplayWeightHistory(weightHistory = listOf(70, 72, 68, 65))
            )
        }
        TODO("Get weight history from data source")
    }

    override fun onSelectLanguageClick() {
        TODO("Not yet implemented")
    }
    override fun onSelectThemeClick() {
        TODO("Not yet implemented")
    }
    override fun displayWeightHistory() {
        // Implementation for displaying weight history
    }
    override fun displayPersonalData() {
        // Implementation for displaying personal data
    }

}