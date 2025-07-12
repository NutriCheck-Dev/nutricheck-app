package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import kotlinx.coroutines.flow.Flow

interface WeightDao : BaseDao<Weight> {

    override suspend fun insert(obj: Weight)

    override suspend fun update(obj: Weight)

    override suspend fun delete(obj: Weight)

    @Query("SELECT * FROM weights ORDER BY enterDate DESC")
    fun getAllWeights(): Flow<List<Weight>>
}