package com.frontend.nutricheck.client.model.services

import com.frontend.nutricheck.client.model.data_layer.History

interface IUserDataService {
    suspend fun getUserHistory(): List<History>
    suspend fun clearUserHistory()
}