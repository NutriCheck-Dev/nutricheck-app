package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import java.util.Date

/**
 *  Represents a [Meal] entity in the database.
 */
@Entity(
    tableName = "meals",
)
data class MealEntity(
    @PrimaryKey val id: String = "",
    val historyDayDate: Date = Date(),
    val dayTime: DayTime = DayTime.BREAKFAST,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double
)