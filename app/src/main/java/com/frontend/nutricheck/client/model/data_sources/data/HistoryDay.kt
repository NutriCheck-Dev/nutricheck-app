package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "histories")
data class HistoryDay(
    @PrimaryKey val date: Date = Date(),
    val calories: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val meals: List<Meal> = emptyList(),
)
