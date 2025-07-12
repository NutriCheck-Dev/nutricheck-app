package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_sources.data.HistoryDay
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HistoryDao : BaseDao<HistoryDay> {

    override suspend fun insert(obj: HistoryDay)

    override suspend fun update(obj: HistoryDay)

    override suspend fun delete(obj: HistoryDay)

    @Query("SELECT * FROM histories WHERE date = :date")
    suspend fun getByDate(date: Date): Flow<HistoryDay>
}