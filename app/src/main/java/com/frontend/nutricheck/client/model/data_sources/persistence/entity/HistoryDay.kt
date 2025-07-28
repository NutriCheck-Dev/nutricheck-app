package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "histories")
data class HistoryDay(
    @PrimaryKey val date: Date = Date(),
)