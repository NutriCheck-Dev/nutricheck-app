package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.IoDispatcher
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of the UserDataRepository interface.
 * Handles user data and weight history operations using DAOs.
 */
class UserDataRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao,
    private val userDataDao: UserDataDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): UserDataRepository {

    override suspend fun getUserData(): UserData {
        return userDataDao.getUserData() ?: UserData()
    }

    override suspend fun getWeightHistory(): List<Weight> {
        return weightDao.getAllWeights().first()
    }

    override suspend fun addWeight(newWeight: Weight) {
        weightDao.insert(newWeight)
    }

    override suspend fun deleteWeight(weight: Weight) {
        weightDao.delete(weight)
    }

    override suspend fun updateUserData(userData: UserData)
    { userDataDao.update(userData) }

    override suspend fun getTargetWeight(): Double {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return userData.targetWeight
        }
        return 0.0
    }

    override suspend fun getDailyCalorieGoal(): Int {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return userData.dailyCaloriesGoal
         }
        return 0
    }

    override suspend fun getNutrientGoal(): List<Int> = withContext(dispatcher) {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return@withContext listOf(userData.carbsGoal, userData.proteinGoal, userData.fatsGoal)
        }
        return@withContext listOf(0, 0, 0)
    }

    override suspend fun addUserDataAndAddWeight(userData: UserData, weight: Weight) {
        userDataDao.insert(userData)
        weightDao.insert(weight)
    }
}