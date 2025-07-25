package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = HistoryDay::class,
            parentColumns = ["date"], // <- das ist dein PK bei HistoryDay
            childColumns = ["historyDayDate"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["historyDayDate"])]
)
data class Meal(
    @PrimaryKey val id: String = "",
    val historyDayDate: Date = Date(),
    val dayTime: DayTime = DayTime.BREAKFAST,
)