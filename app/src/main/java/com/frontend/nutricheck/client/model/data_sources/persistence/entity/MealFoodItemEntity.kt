package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

/**
 * Represents a many-to-many relationship between meals and food products.
 */
@Entity(
    tableName = "meal_food_items",
    primaryKeys = ["mealId", "foodProductId"],
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodProductId"],
        )
    ],
    indices = [
        Index(value = ["mealId"]),
        Index(value = ["foodProductId"])
    ]
)
data class MealFoodItemEntity(
    val mealId: String,
    val foodProductId: String,
    val quantity: Double,
    val servings: Int,
    val servingSize: ServingSize
)