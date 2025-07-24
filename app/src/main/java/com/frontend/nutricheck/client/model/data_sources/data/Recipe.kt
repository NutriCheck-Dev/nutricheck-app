package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey override val id: String = "",
    override val name: String = "Recipe",
    override val calories: Double = 0.0,
    override val carbohydrates: Double = 0.0,
    override val protein: Double = 0.0,
    override val fat: Double = 0.0,
    override val servings: Int = 1,
    val instructions: String = "",
    val visibility: RecipeVisibility = RecipeVisibility.OWNER,
) : FoodComponent
