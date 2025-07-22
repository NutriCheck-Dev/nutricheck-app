package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao,
    private val userDataDao: UserDataDao
): UserDataRepository {
    override suspend fun getUserData(): UserData = userDataDao.getUserData()?: UserData()

    override suspend fun getWeightHistory(): List<Weight> = weightDao.getAllWeights().first()

    override suspend fun getAllWeights(): Flow<List<Weight>> = weightDao.getAllWeights()

    override suspend fun addWeight(weight: Weight) { weightDao.insert(weight) }

    override suspend fun addUserData(userData: UserData) { userDataDao.insert(userData) }

    override suspend fun updateUserData(userData: UserData) { userDataDao.update(userData) }
}