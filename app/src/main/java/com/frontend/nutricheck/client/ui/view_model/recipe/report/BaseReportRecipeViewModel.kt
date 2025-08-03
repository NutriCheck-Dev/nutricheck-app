package com.frontend.nutricheck.client.ui.view_model.recipe.report

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseReportRecipeViewModel : BaseViewModel () {
    abstract fun onInputTextChanged(text: String)
    abstract fun onDismissDialog()
    abstract suspend fun onClickSendReport()
    abstract fun onReportClick(recipe: Recipe)
}