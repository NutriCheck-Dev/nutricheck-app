package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao : BaseDao<RecipeEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(obj: RecipeEntity)
    @Update
    override suspend fun update(obj: RecipeEntity)
    @Delete
    override suspend fun delete(obj: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(recipes: List<RecipeEntity> )

    @Transaction
    @Query("SELECT * " +
            "FROM recipes " +
            "WHERE deleted = 0 AND visibility = :visibility " +
            "ORDER BY name ASC")
    fun getAllRecipesWithIngredients(visibility: RecipeVisibility): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithIngredientsById(recipeId: String): Flow<RecipeWithIngredients>


    @Query("SELECT EXISTS(SELECT 1 FROM foods WHERE id = :id)")
    suspend fun exists(id: String): Boolean
  
    @Transaction
    @Query("SELECT * " +
            "FROM recipes " +
            "WHERE name LIKE '%' || :name || '%' " +
            "AND id NOT IN (SELECT recipeId FROM recipe_search_index)ORDER BY name ASC")
    fun getRecipesByName(name: String): Flow<List<RecipeWithIngredients>>
}