package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight

/**
 * Repository interface for managing user data and weight history.
 */

interface UserDataRepository {
    /**
     * Returns the user data
     */
    suspend fun getUserData() : UserData

    /**
     * Returns the weight history of the user.
     */
    suspend fun getWeightHistory(): List<Weight>
    /**
     * Adds a new weight entry to the user's weight history.
     */
    suspend fun addWeight(weight: Weight)

    /**
     * Deletes a weight entry from the user's weight history.
     */
    suspend fun deleteWeight(weight : Weight)

    /**
     * Updates the existing user data in the repository.
     */
    suspend fun updateUserData(userData: UserData)
    /**
     * Returns the target weight of the user.
     */
    suspend fun getTargetWeight(): Double

    /**
     * Returns the calculated target calorie goal for the user.
     */
    suspend fun getDailyCalorieGoal(): Int
    /**
     * Returns the calculated target nutrients goal for the user.
     */
    suspend fun getNutrientGoal(): List<Int>
    /**
     * Adds user data and a weight entry to the repository.
     * This is used for initial setup when both user data and weight are provided.
     */
    suspend fun addUserDataAndAddWeight(userData: UserData, weight: Weight)
}