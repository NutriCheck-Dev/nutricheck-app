package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealItem

@Entity(
    tableName = "meal_food_items",
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = FoodProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodProductId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["mealId"]),
        Index(value = ["foodProductId"])
    ]
)
data class MealFoodItem(
    @PrimaryKey override val id: String = "",
    override val mealId: String = "",
    val foodProductId: String = "",
    override val quantity: Double = 0.0
) : MealItem