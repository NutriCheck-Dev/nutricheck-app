package com.frontend.nutricheck.client.model.repositories

import com.frontend.nutricheck.client.model.data_layer.History
import com.frontend.nutricheck.client.model.data_layer.Meal
import java.util.Date

interface IHistoryRepository {
    suspend fun getHistoryFromDate(date: Date): History
    suspend fun getMeals(): List<Meal>
    suspend fun removeHistoryEntry(entry: String)
}