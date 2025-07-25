package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId")
    fun getForRecipe(recipeId: String): Flow<List<Ingredient>>

    @Insert
    suspend fun insert(ingredient: Ingredient)

    @Update
    suspend fun update(ingredient: Ingredient)

    @Delete
    suspend fun delete(ingredient: Ingredient)

    @Transaction
    @Query("""
        SELECT * FROM ingredients
        WHERE recipeId = :recipeId
    """)
    fun getIngredientsWithFoodProducts(recipeId: String): Flow<List<IngredientWithFoodProduct>>


}