package com.frontend.nutricheck.client.model.data_sources.persistence.entity.search

import androidx.room.Entity

@Entity(
    tableName = "food_search_index",
    primaryKeys = ["query", "foodProductId"]
)
data class FoodSearchEntity(
    val query: String,
    val foodProductId: String,
    val lastUpdated: Long
)