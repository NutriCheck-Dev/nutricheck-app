package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MealDao : BaseDao<MealEntity> {

    @Insert
    override suspend fun insert(obj: MealEntity)

    @Update
    override suspend fun update(obj: MealEntity)

    @Delete
    override suspend fun delete(obj: MealEntity)
    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteById(mealId: String)

    @Transaction
    @Query("SELECT * FROM meals WHERE historyDayDate = :date")
    suspend fun getMealsWithAllForDay(date: Date): List<MealWithAll>

    @Transaction
    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getById(id: String): MealWithAll

    @Transaction
    @Query("SELECT * FROM meals WHERE historyDayDate = :date")
    fun observeMealsForDay(date: Date): Flow<List<MealWithAll>>

    @Query("SELECT SUM(calories) FROM meals WHERE historyDayDate = :date")
    fun observeCaloriesOfDay(date: Date): Flow<Int>
}