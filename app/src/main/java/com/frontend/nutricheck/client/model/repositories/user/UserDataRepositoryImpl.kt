package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao,
    private val userDataDao: UserDataDao
): UserDataRepository {

    override suspend fun getUserData(): UserData {
         return userDataDao.getUserData()?: UserData()
    }

    override suspend fun getWeightHistory(): List<Float> {
        //TODO("Not yet implemented")
        return emptyList()
    }

    override suspend fun getAllWeights(): Flow<List<Weight>> = weightDao.getAllWeights()

    override suspend fun addWeight(weight: Weight) {
        //TODO("Not yet implemented")
    }

    override suspend fun addUserData(userData: UserData) {
        userDataDao.insert(userData)
    }

    override suspend fun updateUserData(userData: UserData) {
        //TODO("Not yet implemented")
    }

    override suspend fun getTargetWeight(): Double {

        return 80.0
    }

    override suspend fun getCalorieGoal(): Int {
        //TODO("Not yet implemented")
        return 2000
    }

}