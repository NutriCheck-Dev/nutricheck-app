package com.frontend.nutricheck.client.model.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_layer.History

interface HistoryDao : BaseDao<History> {

    override suspend fun insert(obj: History) {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: History) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: History) {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM histories WHERE id = :id")
    suspend fun getById(id: String) : History?
}