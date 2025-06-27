package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodProduct::class,
            parentColumns = ["id"],
            childColumns = ["foodProductId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["foodProductId"])
    ]
)
data class Ingredient (
    @PrimaryKey val id: String = "",
    val recipeId: String = "",
    val foodProductId: String = "",
    val quantity: Double = 0.0,
    val foodProduct: FoodProduct,
)
