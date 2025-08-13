package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Base DAO interface providing common operations for all entities.
 * @param T The entity type
 */
@Dao
interface BaseDao<T> {

    /**
     * Inserts an entity, ignoring conflicts.
     * @param obj The entity to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: T)

    /**
     * Updates an existing entity.
     * @param obj The entity to update
     */
    @Update
    suspend fun update(obj: T)

    /**
     * Deletes an entity.
     * @param obj The entity to delete
     */
    @Delete
    suspend fun delete(obj: T)
}