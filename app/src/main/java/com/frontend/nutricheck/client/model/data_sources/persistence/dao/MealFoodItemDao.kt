package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealFoodItemWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface MealFoodItemDao : BaseDao<MealFoodItemEntity> {

    @Insert
    override suspend fun insert(obj: MealFoodItemEntity)

    @Update
    override suspend fun update(obj: MealFoodItemEntity)

    @Delete
    override suspend fun delete(obj: MealFoodItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mealFoodItems: List<MealFoodItemEntity>)

    @Query("DELETE FROM meal_food_items WHERE mealId = :mealId")
    suspend fun deleteMealFoodItemsOfMeal(mealId: String)

    @Query("DELETE FROM meal_food_items WHERE mealId = :id")
    suspend fun deleteById(id: String)
    @Transaction
    @Query("""
        SELECT *
        FROM meal_food_items
        WHERE mealId = :mealId
        AND foodProductId = :foodProductId
    """)
    suspend fun getItemOfMealById(
        mealId: String,
        foodProductId: String): MealFoodItemWithProduct
}