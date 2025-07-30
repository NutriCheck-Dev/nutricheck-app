package com.frontend.nutricheck.client.model.data_sources.persistence.dao.search

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.search.RecipeSearchEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeSearchDao {

    @Transaction
    @Query("""
        SELECT recipe.*
        FROM recipe_search_index idx
        JOIN recipes AS recipe ON idx.recipeId = recipe.id
        WHERE idx.`query` = :query
        ORDER BY recipe.name
    """)
    fun resultsFor(query: String): Flow<List<RecipeWithIngredients>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntities(entities: List<RecipeSearchEntity>)

    @Query("DELETE FROM recipe_search_index WHERE lastUpdated < :cutoff")
    suspend fun pruneOldEntries(cutoff: Long)

    @Query("DELETE FROM recipe_search_index WHERE `query` = :query")
    suspend fun clearQuery(query: String)

    @Query("""
        SELECT MAX(lastUpdated)
        FROM recipe_search_index
        WHERE `query` = :query
    """)
    suspend fun getLatestUpdatedFor(query: String): Long?
}