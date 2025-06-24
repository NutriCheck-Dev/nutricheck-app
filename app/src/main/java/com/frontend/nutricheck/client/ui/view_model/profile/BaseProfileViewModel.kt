package com.frontend.nutricheck.client.ui.view_model.profile

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseProfileViewModel<DATA>(
    initialData: DATA
) : BaseViewModel () {

    protected val _data = MutableStateFlow(initialData)
    val data: StateFlow<DATA> = _data.asStateFlow()

    abstract fun displayProfile()
    abstract fun onNameClicked(newName: DATA)
    abstract fun onWeightClicked(newWeight: DATA)
    abstract fun onHeightClicked(newHeight: DATA)
    abstract fun selectLanguage()
    abstract fun selectTheme()
    abstract fun addWeight(weight: DATA)
    abstract fun onTargetClicked(newGoal: DATA)
    abstract fun changeTargetWeight(newTargetWeight: DATA)

}