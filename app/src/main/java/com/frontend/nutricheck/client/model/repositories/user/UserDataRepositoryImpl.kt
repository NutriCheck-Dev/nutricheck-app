package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao,
    private val userDataDao: UserDataDao
): UserDataRepository {
    override suspend fun getUserData(): UserData = userDataDao.getUserData()?: UserData()

    override suspend fun getWeightHistory(): List<Weight> = weightDao.getAllWeights().first()

    override suspend fun addWeight(weight: Weight) { weightDao.insert(weight) }

    override suspend fun addUserData(userData: UserData) { userDataDao.insert(userData) }

    override suspend fun updateUserData(userData: UserData) { userDataDao.update(userData) }

    override suspend fun getTargetWeight(): Double {
        //TODO: IMPLEMENT TARGET WEIGHT LOGIC
        return 80.0
    }
    override suspend fun getCalorieGoal(): Int {
        //TODO: IMPLEMENT CALORIE GOAL LOGIC
        return 2000
    }
}