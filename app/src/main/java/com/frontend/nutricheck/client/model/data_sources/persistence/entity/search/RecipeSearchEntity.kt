package com.frontend.nutricheck.client.model.data_sources.persistence.entity.search

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity

/**
 * Represents a search index for recipes.
 */
@Entity(
    tableName = "recipe_search_index",
    primaryKeys = ["query", "recipeId"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [Index("recipeId"), Index("lastUpdated")]
)
data class RecipeSearchEntity(
    val query: String,
    val recipeId: String,
    val lastUpdated: Long
)