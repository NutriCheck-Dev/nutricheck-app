package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weights")
data class Weight(
    val value: Double = 0.0,
    @PrimaryKey val enterDate: Date = Date()
)