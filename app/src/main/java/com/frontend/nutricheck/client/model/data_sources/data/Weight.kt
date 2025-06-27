package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import java.util.Date

@Entity(tableName = "weights")
data class Weight(
    @PrimaryKey val id: String = "",
    val value: Double = 0.0,
    @Contextual val enterDate: Date = Date()
)
