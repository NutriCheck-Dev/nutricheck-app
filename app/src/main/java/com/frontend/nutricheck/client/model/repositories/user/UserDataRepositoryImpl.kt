package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of the UserDataRepository interface.
 * Handles user data and weight history operations using DAOs.
 */
class UserDataRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao,
    private val userDataDao: UserDataDao
): UserDataRepository {

    override suspend fun getUserData(): UserData = withContext(Dispatchers.IO) {
        userDataDao.getUserData() ?: UserData()
    }

    override suspend fun getWeightHistory(): List<Weight> = withContext(Dispatchers.IO) {
        weightDao.getAllWeights().first()
    }

    override suspend fun addWeight(newWeight: Weight) = withContext(Dispatchers.IO) {
        weightDao.insert(newWeight)
    }

    override suspend fun deleteWeight(weight: Weight) = withContext(Dispatchers.IO) {
        weightDao.delete(weight)
    }

    override suspend fun updateUserData(userData: UserData) = withContext(Dispatchers.IO)
    { userDataDao.update(userData) }

    override suspend fun getTargetWeight(): Double = withContext(Dispatchers.IO) {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return@withContext userData.targetWeight
        }
        return@withContext 0.0
    }

    override suspend fun getDailyCalorieGoal(): Int = withContext(Dispatchers.IO) {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return@withContext userData.dailyCaloriesGoal
         }
        return@withContext 0
    }

    override suspend fun getNutrientGoal(): List<Int> = withContext(Dispatchers.IO) {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return@withContext listOf(userData.carbsGoal, userData.proteinGoal, userData.fatsGoal)
        }
        return@withContext listOf(0, 0, 0)
    }

    override suspend fun addUserDataAndAddWeight(userData: UserData, weight: Weight) =
        withContext(Dispatchers.IO) {
        userDataDao.insert(userData)
        weightDao.insert(weight)
    }
}