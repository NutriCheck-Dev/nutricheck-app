package com.frontend.nutricheck.client.model.data_sources.persistence.entity.search

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity

@Entity(
    tableName = "food_search_index",
    primaryKeys = ["query", "foodProductId"],
    foreignKeys = [
        ForeignKey(
            entity = FoodProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodProductId"],
            onDelete = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [Index("foodProductId"), Index("lastUpdated")]
)
data class FoodSearchEntity(
    val query: String,
    val foodProductId: String,
    val lastUpdated: Long
)