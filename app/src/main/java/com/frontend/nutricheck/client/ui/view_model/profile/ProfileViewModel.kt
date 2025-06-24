package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.model.data_layer.UserData
import com.frontend.nutricheck.client.ui.view_model.profile.BaseProfileViewModel

class ProfileViewModel : BaseProfileViewModel<UserData>(UserData()) {
    override fun onNameClicked(newName: UserData) {
        TODO("Not yet implemented")
    }

    override fun onWeightClicked(newWeight: UserData) {
        TODO("Not yet implemented")
    }
    override fun onHeightClicked(newHeight: UserData) {
        TODO("Not yet implemented")
    }
    override fun selectLanguage() {
        TODO("Not yet implemented")
    }
    override fun selectTheme() {
        TODO("Not yet implemented")
    }
    override fun addWeight(weight: UserData) {
        TODO("Not yet implemented")
    }
    override fun onTargetClicked(newTarget: UserData) {
        TODO("Not yet implemented")
    }
    override fun changeTargetWeight(newTargetWeight: UserData) {
        TODO("Not yet implemented")
    }
}