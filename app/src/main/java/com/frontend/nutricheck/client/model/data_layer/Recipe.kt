package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable @Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey override val id: String = "",
    override val name: String = "",
    override val calories: Int = 0,
    override val protein: Int = 0,
    override val carbohydrates: Int = 0,
    override val fat: Int = 0,
    val servings: Int = 1,
    val ingredients: Set<Food> = emptySet(),
    val instructions: String? = null,
) : FoodComponent
