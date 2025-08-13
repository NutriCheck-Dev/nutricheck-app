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
/**
 * Data Access Object for managing ingredient-related database operations.
 *
 * @see IngredientEntity The entity representing an ingredient in the database
 * @see IngredientWithFoodProduct The relation object containing ingredient and associated food product data
 */
@Dao
interface IngredientDao {
    /**
     * Inserts a new ingredient into the database.
     *
     * @param ingredientEntity The ingredient entity to
     */
    @Insert
    suspend fun insert(ingredientEntity: IngredientEntity)
    /**
     * Updates an existing ingredient in the database.
     *
     * @param ingredientEntity The ingredient entity with updated values
     */
    @Update
    suspend fun update(ingredientEntity: IngredientEntity)
    /**
     * Deletes an ingredient from the database.
     *
     * @param ingredientEntity The ingredient entity to delete
     */
    @Delete
    suspend fun delete(ingredientEntity: IngredientEntity)

    /**
     * Retrieves all ingredients for a specific recipe along with their associated food product information.
     *
     * @param recipeId The unique identifier of the recipe
     * @return List of ingredients with their associated food product data.
     *         Returns an empty list if no ingredients are found for the recipe.
     */
    @Transaction
    @Query("""
        SELECT * FROM ingredients
        WHERE recipeId = :recipeId
    """)
    fun getIngredientsWithFoodProducts(recipeId: String): List<IngredientWithFoodProduct>
    /**
     * Inserts multiple ingredients into the database in a single operation.
     * If any ingredient conflicts with existing data, the conflicting ingredient will be replaced
     * with the new data.
     *
     * @param ingredients List of ingredient entities to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientEntity>)
    /**
     * Deletes all ingredients associated with a specific recipe.
     *
     * @param recipeId The unique identifier of the recipe whose ingredients should be deleted
     */
    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsOfRecipe(recipeId: String)
    /**
     * Retrieves a specific ingredient by recipe ID and food product ID, including food product details.
     *
     * @param recipeId The unique identifier of the recipe
     * @param foodProductId The unique identifier of the food product
     * @return The ingredient with food product data if found, null otherwise
     */
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
    ): IngredientWithFoodProduct?
}