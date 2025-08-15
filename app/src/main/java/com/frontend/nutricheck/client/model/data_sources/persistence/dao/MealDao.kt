package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * DAO for managing meal records in the database.
 */
@Dao
interface MealDao : BaseDao<MealEntity> {

    /**
     * Inserts a new meal record.
     * @param obj The meal entity to insert
     */
    @Insert
    override suspend fun insert(obj: MealEntity)

    /**
     * Updates an existing meal record.
     * @param obj The meal entity to update
     */
    @Update
    override suspend fun update(obj: MealEntity)

    /**
     * Deletes a meal record.
     * @param obj The meal entity to delete
     */
    @Delete
    override suspend fun delete(obj: MealEntity)

    /**
     * Deletes a meal by its ID.
     * @param mealId The ID of the meal to delete
     */
    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteById(mealId: String)

    /**
     * Gets all meals for a specific date with complete nutrition data.
     * @param date The date to query meals for
     * @return List of meals with all related data
     */
    @Transaction
    @Query("SELECT * FROM meals WHERE historyDayDate = :date")
    suspend fun getMealsWithAllForDay(date: Date): List<MealWithAll>

    /**
     * Gets a meal by ID with complete nutrition data.
     * @param id The meal ID
     * @return Meal with all related data
     */
    @Transaction
    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getById(id: String): MealWithAll

    @Transaction
    @Query("SELECT * FROM meals WHERE historyDayDate = :date")
    fun observeMealsForDay(date: Date): Flow<List<MealWithAll>>

    @Query("SELECT SUM(calories) FROM meals WHERE historyDayDate = :date")
    fun observeCaloriesOfDay(date: Date): Flow<Int>
}