package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_sources.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataDao : BaseDao<UserData> {

    override suspend fun insert(obj: UserData)

    override suspend fun update(obj: UserData)

    override suspend fun delete(obj: UserData)

    @Query("SELECT * FROM user_data LIMIT 1")
    suspend fun getUserData(): Flow<UserData>
}