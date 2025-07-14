package com.frontend.nutricheck.client.ui.view_model.profile

import com.frontend.nutricheck.client.model.data_sources.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserDataState(
    val userData: UserData = UserData()
)

sealed interface EditPersonalDataEvent {
    data class SaveData(val userData: UserData) : EditPersonalDataEvent
    data object EditClick : EditPersonalDataEvent
    data object CancelClick : EditPersonalDataEvent
}

@HiltViewModel
class EditPersonalDataViewModel @Inject constructor(
    private val initialState: UserDataState = UserDataState()
) : BaseEditPersonalDataViewModel<UserData>(
    UserData()
) {

    private val _userDataState = MutableStateFlow(UserDataState())
    val createRecipeState = _userDataState.asStateFlow()

    val _events = MutableSharedFlow<EditPersonalDataEvent>()
    val events: SharedFlow<EditPersonalDataEvent> = _events.asSharedFlow()

    fun onEvent(event: EditPersonalDataEvent) {}

    override fun validate(data: UserData): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun persistData(data: UserData): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun onEditClick() {
        TODO("Not yet implemented")
    }
}