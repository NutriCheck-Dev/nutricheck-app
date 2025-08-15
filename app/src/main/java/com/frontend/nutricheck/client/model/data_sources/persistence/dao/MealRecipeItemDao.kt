package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealRecipeItemWithRecipe

/**
 * DAO for managing meal recipe items in the database.
 */
@Dao
interface MealRecipeItemDao : BaseDao<MealRecipeItemEntity> {

    /**
     * Inserts a new meal recipe item.
     * @param obj The meal recipe item entity to insert
     */
    @Insert
    override suspend fun insert(obj: MealRecipeItemEntity)

    /**
     * Updates an existing meal recipe item.
     * @param obj The meal recipe item entity to update
     */
    @Update
    override suspend fun update(obj: MealRecipeItemEntity)

    /**
     * Deletes a meal recipe item.
     * @param obj The meal recipe item entity to delete
     */
    @Delete
    override suspend fun delete(obj: MealRecipeItemEntity)

    /**
     * Inserts multiple meal recipe items, replacing existing ones.
     * @param mealFoodItems List of meal recipe items to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mealFoodItems: List<MealRecipeItemEntity>)

    /**
     * Gets meal recipe items by recipe ID.
     * @param recipeId The recipe ID to query
     * @return List of meal recipe items for the recipe
     */
    @Query("SELECT * FROM meal_recipe_items WHERE recipeId = :recipeId")
    fun getById(recipeId: String): List<MealRecipeItemEntity>

    /**
     * Deletes all recipe items for a specific meal.
     * @param mealId The meal ID
     */
    @Query("DELETE FROM meal_recipe_items WHERE mealId = :mealId")
    suspend fun deleteMealRecipeItemsOfMeal(mealId: String)

    /**
     * Deletes meal recipe items by meal ID.
     * @param id The meal ID
     */
    @Query("DELETE FROM meal_recipe_items WHERE mealId = :id")
    suspend fun deleteById(id: String)

    /**
     * Gets meal recipe item with complete recipe data by meal ID.
     * @param mealId The meal ID
     * @return Meal recipe item with recipe details
     */
    @Transaction
    @Query("""
        SELECT *
        FROM meal_recipe_items
        WHERE mealId = :mealId
    """)
    suspend fun getItemOfMealById(
        mealId: String
    ): MealRecipeItemWithRecipe
}