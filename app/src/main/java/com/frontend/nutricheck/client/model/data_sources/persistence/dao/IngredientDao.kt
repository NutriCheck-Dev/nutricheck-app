package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    @Insert
    suspend fun insert(ingredientEntity: IngredientEntity)

    @Update
    suspend fun update(ingredientEntity: IngredientEntity)

    @Delete
    suspend fun delete(ingredientEntity: IngredientEntity)

    @Transaction
    @Query("""
        SELECT * FROM ingredients
        WHERE recipeId = :recipeId
    """)
    fun getIngredientsWithFoodProducts(recipeId: String): List<IngredientWithFoodProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientEntity>)

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsOfRecipe(recipeId: String)

    @Transaction
    @Query("""
        SELECT *
        FROM ingredients
        WHERE recipeId = :recipeId 
        AND foodProductId = :foodProductId
    """)
    suspend fun getIngredientById(
        recipeId: String,
        foodProductId: String
    ): Flow<IngredientWithFoodProduct?>

}