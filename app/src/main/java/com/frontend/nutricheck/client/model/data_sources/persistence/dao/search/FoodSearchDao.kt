package com.frontend.nutricheck.client.model.data_sources.persistence.dao.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.search.FoodSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodSearchDao {

    @Transaction
    @Query("""
        SELECT foodProduct.*
        FROM food_search_index idx
        JOIN foods AS foodProduct ON idx.foodProductId = foodProduct.id
        WHERE idx.`query` = :query
        ORDER BY foodProduct.name
    """)
    fun resultsFor(query: String): Flow<List<FoodProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntities(entities: List<FoodSearchEntity>)

    @Query("DELETE FROM food_search_index WHERE lastUpdated < :cutoff")
    suspend fun pruneOldEntries(cutoff: Long)

    @Query("DELETE FROM food_search_index WHERE `query` = :query")
    suspend fun clearQuery(query: String)

    @Query("""
        SELECT MAX(lastUpdated)
        FROM food_search_index
        WHERE `query` = :query
    """)
    suspend fun getLatestUpdatedFor(query: String): Long?
}