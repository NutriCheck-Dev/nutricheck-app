package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing weight records in the database.
 */
@Dao
interface WeightDao : BaseDao<Weight> {

    /**
     * Inserts or replaces a weight record.
     * @param obj The weight record to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(obj: Weight)

    /**
     * Updates an existing weight record.
     * @param obj The weight record to update
     */
    @Update
    override suspend fun update(obj: Weight)

    /**
     * Deletes a weight record.
     * @param obj The weight record to delete
     */
    @Delete
    override suspend fun delete(obj: Weight)

    /**
     * Gets all weight records ordered by date (newest first).
     * @return Flow of weight records list
     */
    @Query("SELECT * FROM weights ORDER BY date DESC")
    fun getAllWeights(): Flow<List<Weight>>
}