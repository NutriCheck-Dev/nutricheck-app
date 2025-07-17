package com.frontend.nutricheck.client.ui.view_model.profile

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseProfileOverviewViewModel<DATA>(
    initialData: DATA
) : BaseViewModel() {

    private val _data = MutableStateFlow(initialData)
    open val data: StateFlow<DATA> = _data.asStateFlow()

    abstract fun onPersonalDataClick()
    abstract fun onWeightHistoryClick()
    abstract fun onSaveLanguageClick(language: String)
    abstract fun onChangeThemeClick(theme : String)
    abstract fun displayWeightHistory()
    abstract fun displayPersonalData()
    abstract fun editLanguage()

}