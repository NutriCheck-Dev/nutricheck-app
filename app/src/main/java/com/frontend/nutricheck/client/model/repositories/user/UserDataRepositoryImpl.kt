package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import kotlinx.coroutines.flow.Flow

class UserDataRepositoryImpl(
    private val weightDao: WeightDao,
    private val userDataDao: UserDataDao
): UserDataRepository {

    override suspend fun getUserData(): List<UserData> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeightHistory(): List<Weight> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWeights(): Flow<List<Weight>> = weightDao.getAllWeights()
    override suspend fun addWeight(weight: Weight) {
        TODO("Not yet implemented")
    }

    override suspend fun addUserData(userData: UserData) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserData(userData: UserData) {
        TODO("Not yet implemented")
    }

}