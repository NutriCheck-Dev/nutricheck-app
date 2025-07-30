package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    suspend fun getUserData() : UserData
    suspend fun getWeightHistory(): List<Weight>
    suspend fun addWeight(weight: Weight)
    suspend fun addUserData(userData: UserData)
    suspend fun updateUserData(userData: UserData)
    suspend fun getTargetWeight(): Double
    suspend fun getDailyCalorieGoal(): Int
    suspend fun getNutrientGoal(): List<Int>
}