package com.frontend.nutricheck.client.model.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_layer.Weight

interface WeightDao : BaseDao<Weight> {

    override suspend fun insert(obj: Weight) {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: Weight) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: Weight) {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM weights")
    fun getAllWeights(): List<Weight>
}