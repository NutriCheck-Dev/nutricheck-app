package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao : BaseDao<Recipe> {
    @Insert
    override suspend fun insert(obj: Recipe)

    @Update
    override suspend fun update(obj: Recipe)
    @Delete
    override suspend fun delete(obj: Recipe)

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getById(id: String): Flow<Recipe>

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAll(): Flow<List<Recipe>>

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllMyRecipes(): Flow<List<Recipe>>

    @Transaction
    @Query("SELECT * FROM recipes")
    fun getAllRecipesWithIngredients(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithIngredientsById(recipeId: String): Flow<RecipeWithIngredients>
}