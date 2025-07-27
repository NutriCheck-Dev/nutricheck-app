package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao : BaseDao<RecipeEntity> {
    @Insert
    override suspend fun insert(obj: RecipeEntity)
    @Update
    override suspend fun update(obj: RecipeEntity)
    @Delete
    override suspend fun delete(obj: RecipeEntity)

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipesWithIngredients(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithIngredientsById(recipeId: String): Flow<RecipeWithIngredients>
}