package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import java.util.Date

@Entity(tableName = "weights")
data class Weight(
    @PrimaryKey val id: String = "",
    val value: Float = 0f,
    @Contextual val enterDate: Date = Date()
)
