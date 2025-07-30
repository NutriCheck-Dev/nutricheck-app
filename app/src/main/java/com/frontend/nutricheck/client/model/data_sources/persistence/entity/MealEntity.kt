package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import java.util.Date

@Entity(
    tableName = "meals",
)
data class MealEntity(
    @PrimaryKey val id: String = "", //date as key?, search by date? ->
    val historyDayDate: Date = Date(), //is already in parent -> why table HistoryDay?
    val dayTime: DayTime = DayTime.BREAKFAST,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double
)