package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject
/**
 * Implementation of the UserDataRepository interface.
 * Handles user data and weight history operations using DAOs.
 */
class UserDataRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao,
    private val userDataDao: UserDataDao
): UserDataRepository {
    /**
     * Retrieves the current user data.
     * @return UserData object or a default UserData if none exists.
     */
    override suspend fun getUserData(): UserData = userDataDao.getUserData()?: UserData()
    /**
     * Gets the complete weight history for the user.
     * @return List of Weight objects.
     */
    override suspend fun getWeightHistory(): List<Weight> = weightDao.getAllWeights().first()
    /**
     * Adds a new weight entry or updates an existing one if the date matches.
     * @param newWeight The Weight object to add or update.
     */
    override suspend fun addWeight(newWeight: Weight) { weightDao.insert(newWeight) }
    /**
     * Inserts new user data into the database.
     * @param userData The UserData object to insert.
     */
    override suspend fun addUserData(userData: UserData) { userDataDao.insert(userData) }
    /**
     * Updates existing user data in the database.
     * @param userData The UserData object to update.
     */
    override suspend fun updateUserData(userData: UserData) { userDataDao.update(userData) }
    /**
     * Retrieves the user's target weight.
     * @return Target weight as Double, or 0.0 if not set.
     */
    override suspend fun getTargetWeight(): Double {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return userData.targetWeight
        }
        return 0.0
    }
    /**
     * Retrieves the user's daily calorie goal.
     * @return Daily calorie goal as Int, or 0 if not set.
     */
    override suspend fun getDailyCalorieGoal(): Int {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return userData.dailyCaloriesGoal
        }
        return 0
    }
    /**
     * Retrieves the user's nutrient goals (carbs, protein, fats).
     * @return List of Ints representing nutrient goals.
     */
    override suspend fun getNutrientGoal(): List<Int> {
        val userData = userDataDao.getUserData()
        if (userData != null) {
            return listOf(userData.carbsGoal, userData.proteinGoal, userData.fatsGoal)
        }
        return listOf(0, 0, 0)
    }

}