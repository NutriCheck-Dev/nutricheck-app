package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

@Entity(
    tableName = "ingredients",
    primaryKeys = ["recipeId", "foodProductId"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodProductId"]
        )
    ],
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["foodProductId"])
    ]
)
data class IngredientEntity (
    val recipeId: String,
    val foodProductId: String,
    val quantity: Double,
    val servings: Int,
    val servingSize: ServingSize
)