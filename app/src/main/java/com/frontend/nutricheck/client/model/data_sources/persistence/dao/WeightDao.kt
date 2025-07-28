package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao : BaseDao<Weight> {

    @Insert
    override suspend fun insert(obj: Weight)

    @Update
    override suspend fun update(obj: Weight)

    @Delete
    override suspend fun delete(obj: Weight)

    @Query("SELECT * FROM weights ORDER BY enterDate DESC")
    fun getAllWeights(): Flow<List<Weight>>
}