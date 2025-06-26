package com.frontend.nutricheck.client.model.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_layer.HistoryDay

interface HistoryDao : BaseDao<HistoryDay> {

    override suspend fun insert(obj: HistoryDay) {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: HistoryDay) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: HistoryDay) {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM histories WHERE id = :id")
    suspend fun getById(id: String) : HistoryDay?
}