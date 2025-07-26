package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem

@Dao
interface MealFoodItemDao : BaseDao<MealFoodItem> {

    @Insert
    override suspend fun insert(obj: MealFoodItem)

    @Update
    override suspend fun update(obj: MealFoodItem)

    @Delete
    override suspend fun delete(obj: MealFoodItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mealFoodItems: List<MealFoodItem>)
}