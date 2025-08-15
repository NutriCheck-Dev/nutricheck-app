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

/**
 * DAO for managing meal food items in the database.
 */
@Dao
interface MealFoodItemDao : BaseDao<MealFoodItemEntity> {

    /**
     * Inserts a new meal food item.
     * @param obj The meal food item entity to insert
     */
    @Insert
    override suspend fun insert(obj: MealFoodItemEntity)

    /**
     * Updates an existing meal food item.
     * @param obj The meal food item entity to update
     */
    @Update
    override suspend fun update(obj: MealFoodItemEntity)

    /**
     * Deletes a meal food item.
     * @param obj The meal food item entity to delete
     */
    @Delete
    override suspend fun delete(obj: MealFoodItemEntity)

    /**
     * Inserts multiple meal food items, replacing existing ones.
     * @param mealFoodItems List of meal food items to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mealFoodItems: List<MealFoodItemEntity>)

    /**
     * Deletes all food items for a specific meal.
     * @param mealId The meal ID
     */
    @Query("DELETE FROM meal_food_items WHERE mealId = :mealId")
    suspend fun deleteMealFoodItemsOfMeal(mealId: String)

    /**
     * Deletes meal food items by meal ID.
     * @param id The meal ID
     */
    @Query("DELETE FROM meal_food_items WHERE mealId = :id")
    suspend fun deleteById(id: String)

    /**
     * Gets meal food item with product data by meal and food product ID.
     * @param mealId The meal ID
     * @param foodProductId The food product ID
     * @return Meal food item with product details
     */
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