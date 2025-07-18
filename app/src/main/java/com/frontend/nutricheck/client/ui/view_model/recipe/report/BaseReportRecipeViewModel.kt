package com.frontend.nutricheck.client.ui.view_model.recipe.report

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseReportRecipeViewModel : BaseViewModel () {
    abstract fun onInputTextChanged(text: String)
    abstract fun onReportClick()
    abstract suspend fun onClickSendReport()
}