package com.frontend.nutricheck.client.model.data_sources.persistence.entity.search

import androidx.room.Entity

@Entity(
    tableName = "recipe_search_index",
    primaryKeys = ["query", "recipeId"]
)
data class RecipeSearchEntity(
    val query: String,
    val recipeId: String,
    val lastUpdated: Long
)