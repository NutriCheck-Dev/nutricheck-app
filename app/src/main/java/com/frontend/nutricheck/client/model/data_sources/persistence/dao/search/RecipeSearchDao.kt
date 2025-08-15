package com.frontend.nutricheck.client.model.data_sources.persistence.dao.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.search.RecipeSearchEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing recipe search cache in the database.
 */
@Dao
interface RecipeSearchDao {

    /**
     * Gets cached search results for a specific query.
     * @param query The search query
     * @return Flow of recipes with ingredients matching the query, ordered by name
     */
    @Transaction
    @Query("""
        SELECT recipe.*
        FROM recipe_search_index idx
        JOIN recipes AS recipe ON idx.recipeId = recipe.id
        WHERE idx.`query` = :query
        ORDER BY recipe.name
    """)
    fun resultsFor(query: String): Flow<List<RecipeWithIngredients>>

    /**
     * Inserts or updates search cache entries.
     * @param entities List of search entities to cache
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntities(entities: List<RecipeSearchEntity>)

    /**
     * Removes old search cache entries.
     * @param cutoff Timestamp cutoff for entries to remove
     */
    @Query("DELETE FROM recipe_search_index WHERE lastUpdated < :cutoff")
    suspend fun pruneOldEntries(cutoff: Long)

    /**
     * Clears all cached results for a specific query.
     * @param query The query to clear from cache
     */
    @Query("DELETE FROM recipe_search_index WHERE `query` = :query")
    suspend fun clearQuery(query: String)

    /**
     * Gets the latest update timestamp for a query.
     * @param query The query to check
     * @return Latest update timestamp, or null if query not cached
     */
    @Query("""
        SELECT MAX(lastUpdated)
        FROM recipe_search_index
        WHERE `query` = :query
    """)
    suspend fun getLatestUpdatedFor(query: String): Long?
}