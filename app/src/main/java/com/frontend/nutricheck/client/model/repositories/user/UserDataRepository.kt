package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    suspend fun getUserData() : UserData
    suspend fun getWeightHistory(): List<Weight>
    suspend fun addWeight(weight: Weight)
    suspend fun addUserData(userData: UserData)
    suspend fun updateUserData(userData: UserData)
    suspend fun getTargetWeight(): Double
    suspend fun getCalorieGoal(): Int
}