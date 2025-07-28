package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.frontend.nutricheck.client.model.data_sources.data.DayTime
import java.util.Date

@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = HistoryDay::class,
            parentColumns = ["date"], // <- das ist dein PK bei HistoryDay
            childColumns = ["historyDayDate"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["historyDayDate"])]
)
data class MealEntity(
    @PrimaryKey val id: String = "", //date as key?, search by date? ->
    val historyDayDate: Date = Date(), //is already in parent -> why table HistoryDay?
    val dayTime: DayTime = DayTime.BREAKFAST,
    //nutriments...
)