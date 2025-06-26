package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient (
    @PrimaryKey val recipeId: String = "",
    val foodProductId: String = "",
    val foodProduct: FoodProduct = FoodProduct(),
    val quantity: Double = 0.0,
)
