package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
/**
 * Data Access Object (DAO) for performing database operations on UserData entities.
 */
@Dao
interface UserDataDao : BaseDao<UserData> {
    /**
     * Inserts a new UserData entity into the database.
     * @param obj The UserData object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(obj: UserData)
    /**
     * Updates an existing UserData entity in the database.
     * @param obj The UserData object to update.
     */
    @Update
    override suspend fun update(obj: UserData)
    /**
     * Deletes a UserData entity from the database.
     * @param obj The UserData object to delete.
     */
    @Delete
    override suspend fun delete(obj: UserData)
    /**
     * Retrieves the first UserData entity from the database.
     * @return The UserData object if found, or null otherwise.
     */
    @Query("SELECT * FROM user_data LIMIT 1")
    suspend fun getUserData(): UserData?
}