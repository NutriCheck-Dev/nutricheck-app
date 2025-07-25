package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MealDao : BaseDao<Meal> {

    @Insert
    override suspend fun insert(obj: Meal)

    @Update
    override suspend fun update(obj: Meal)

    @Delete
    override suspend fun delete(obj: Meal)

    @Transaction
    @Query("SELECT * FROM meals WHERE historyDayDate = :date")
    suspend fun getMealsWithAllForDay(date: Date): List<MealWithAll>
}