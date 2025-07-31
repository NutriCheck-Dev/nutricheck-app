package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val calories: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val servings: Double = 0.0,
    val instructions: String = "",
    val visibility: RecipeVisibility = RecipeVisibility.PUBLIC
)