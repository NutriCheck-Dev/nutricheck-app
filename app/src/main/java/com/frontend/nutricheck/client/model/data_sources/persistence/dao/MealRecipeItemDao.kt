package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity

@Dao
interface MealRecipeItemDao : BaseDao<MealRecipeItemEntity> {

    @Insert
    override suspend fun insert(obj: MealRecipeItemEntity)

    @Update
    override suspend fun update(obj: MealRecipeItemEntity)

    @Delete
    override suspend fun delete(obj: MealRecipeItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mealFoodItems: List<MealRecipeItemEntity>)

    @Query("SELECT * FROM meal_recipe_items WHERE recipeId = :recipeId")
    fun getById(recipeId: String): List<MealRecipeItemEntity>?
}