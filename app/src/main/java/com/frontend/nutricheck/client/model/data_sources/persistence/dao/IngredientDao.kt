package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity

@Dao
interface IngredientDao {

    @Insert
    suspend fun insert(ingredientEntity: IngredientEntity)

    @Update
    suspend fun update(ingredientEntity: IngredientEntity)

    @Delete
    suspend fun delete(ingredientEntity: IngredientEntity)

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsOfRecipe(recipeId: String)

}