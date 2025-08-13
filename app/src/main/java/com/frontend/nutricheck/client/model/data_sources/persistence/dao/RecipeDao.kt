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

/**
 * DAO for managing recipe records in the database.
 */
@Dao
interface RecipeDao : BaseDao<RecipeEntity> {

    /**
     * Inserts or replaces a recipe.
     * @param obj The recipe entity to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(obj: RecipeEntity)

    /**
     * Updates an existing recipe.
     * @param obj The recipe entity to update
     */
    @Update
    override suspend fun update(obj: RecipeEntity)

    /**
     * Deletes a recipe.
     * @param obj The recipe entity to delete
     */
    @Delete
    override suspend fun delete(obj: RecipeEntity)

    /**
     * Inserts multiple recipes, aborting on conflicts.
     * @param recipes List of recipes to insert
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    /**
     * Gets all non-deleted recipes with ingredients by visibility.
     * @param visibility The recipe visibility filter
     * @return Flow of recipes with ingredients, ordered by name
     */
    @Transaction
    @Query("SELECT * " +
            "FROM recipes " +
            "WHERE deleted = 0 AND visibility = :visibility " +
            "ORDER BY name ASC")
    fun getAllRecipesWithIngredients(visibility: RecipeVisibility): Flow<List<RecipeWithIngredients>>

    /**
     * Gets a recipe with ingredients by ID.
     * @param recipeId The recipe ID
     * @return Flow of recipe with ingredients
     */
    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithIngredientsById(recipeId: String): Flow<RecipeWithIngredients>

    /**
     * Checks if a recipe exists.
     * @param id The recipe ID to check
     * @return True if the recipe exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM recipes WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    /**
     * Searches recipes by name, excluding indexed recipes.
     * @param name The name to search for (partial matching)
     * @return Flow of matching recipes with ingredients, ordered by name
     */
    @Transaction
    @Query("SELECT * " +
            "FROM recipes " +
            "WHERE name LIKE '%' || :name || '%' " +
            "AND id NOT IN (SELECT recipeId FROM recipe_search_index) ORDER BY name ASC")
    fun getRecipesByName(name: String): Flow<List<RecipeWithIngredients>>
}