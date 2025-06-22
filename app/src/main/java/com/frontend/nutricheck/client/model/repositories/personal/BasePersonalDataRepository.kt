package com.frontend.nutricheck.client.model.repositories.personal

import com.frontend.nutricheck.client.model.data_layer.UserData

interface BasePersonalDataRepository {
    suspend fun load(): UserData
    suspend fun save(userData: UserData)
    suspend fun update(userData: UserData)
}