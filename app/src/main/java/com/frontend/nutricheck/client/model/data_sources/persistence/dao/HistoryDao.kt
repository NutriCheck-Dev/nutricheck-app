package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.HistoryDay
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HistoryDao : BaseDao<HistoryDay> {

    @Insert
    override suspend fun insert(obj: HistoryDay)

    @Update
    override suspend fun update(obj: HistoryDay)

    @Delete
    override suspend fun delete(obj: HistoryDay)

    @Query("SELECT * FROM histories WHERE date = :date")
    fun getByDate(date: Date): Flow<HistoryDay>
}