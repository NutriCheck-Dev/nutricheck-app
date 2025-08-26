package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
/**
 * Entity class representing a user's weight entry.[]
 * Each entry stores the weight value and the date it was recorded.
 */
@Entity(tableName = "weights")
data class Weight(
    val value: Double = 0.0,
    @PrimaryKey val date: Date = Date()
)