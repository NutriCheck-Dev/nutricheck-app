package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_layer.UserData
import com.frontend.nutricheck.client.model.data_layer.Weight

interface UserDataRepository {
    suspend fun getUserData() : List<UserData>
    suspend fun getWeightHistory(): List<Weight>
    suspend fun getAllWeights(): List<Weight>?
    suspend fun addWeight(weight: Weight)
    suspend fun addUserData(userData: UserData)
    suspend fun updateUserData(userData: UserData)
}