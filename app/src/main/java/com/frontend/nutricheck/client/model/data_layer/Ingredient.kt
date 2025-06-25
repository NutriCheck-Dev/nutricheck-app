package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable @Entity(tableName = "ingredients")
data class Ingredient (
    @PrimaryKey val recipeId: String = "",
    val foodProductId: String,
    val foodProduct: FoodProduct,
    val quantity: Double
)
