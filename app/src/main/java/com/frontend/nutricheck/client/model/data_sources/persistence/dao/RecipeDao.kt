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

data class VisibilityById(
    val id: String,
    val visibility: RecipeVisibility
)

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


    @Query("SELECT EXISTS(SELECT 1 FROM recipes WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(recipe: RecipeEntity): Long

    @Query(
        """
        UPDATE recipes SET
        name = :name,
        instructions = :instructions,
        calories = :calories,
        visibility = :visibility
        WHERE id = :id AND visibility != :ownerVisibility
        """
    )
    suspend fun updateIfNotOwner(
        id: String,
        name: String,
        instructions: String?,
        calories: Double,
        visibility: RecipeVisibility,
        ownerVisibility: RecipeVisibility = RecipeVisibility.OWNER
    )

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeEntityById(id: String): RecipeEntity?

    @Query("SELECT id, visibility FROM recipes WHERE id IN (:ids)")
    suspend fun getVisibilityById(ids: List<String>): List<VisibilityById>

    @Query("""
        DELETE FROM recipes
        WHERE
        visibility != :visibility
        AND NOT EXISTS (SELECT 1 FROM meal_recipe_items mri WHERE mri.recipeId = recipes.id)
        AND COALESCE((
            SELECT MAX(s.lastUpdated)
            FROM recipe_search_index s
            WHERE s.recipeId = recipes.id
        ), 0) < :cutoff
    """)
    suspend fun deleteExpiredUnreferencedRecipes(
        cutoff: Long,
        visibility: RecipeVisibility = RecipeVisibility.OWNER
    ): Int

}