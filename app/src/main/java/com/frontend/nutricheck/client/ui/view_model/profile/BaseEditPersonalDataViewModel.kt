package com.frontend.nutricheck.client.ui.view_model.profile

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseEditPersonalDataViewModel<DATA>(
    initialData: DATA
) : BaseViewModel() {

    private val _data = MutableStateFlow(initialData)
    val data: StateFlow<DATA> = _data.asStateFlow()

    abstract fun validate(data: DATA): Boolean
    abstract suspend fun persistData(data: DATA): Result<Unit>
    abstract fun onEditClick()
    protected fun updateData(newData: DATA) {
        _data.value = newData
    }

}