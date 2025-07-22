package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao : BaseDao<UserData> {

    @Insert
    override suspend fun insert(obj: UserData)

    @Update
    override suspend fun update(obj: UserData)

    @Delete
    override suspend fun delete(obj: UserData)

    @Query("SELECT * FROM user_data LIMIT 1")
    suspend fun getUserData(): UserData?
}