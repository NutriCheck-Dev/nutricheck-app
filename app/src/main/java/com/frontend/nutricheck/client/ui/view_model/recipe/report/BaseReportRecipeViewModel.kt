package com.frontend.nutricheck.client.ui.view_model.recipe.report

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseReportRecipeViewModel : BaseViewModel () {
    abstract fun onInputTextChanged(text: String)
    abstract fun onReportClick()
    abstract fun onDismissDialog()
    abstract suspend fun onClickSendReport()
}