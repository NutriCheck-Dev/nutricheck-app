package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "meal_recipe_items",
    primaryKeys = ["mealId", "recipeId"],
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
        )
    ],
    indices = [
        Index(value = ["mealId"]),
        Index(value = ["recipeId"])
    ]
)
data class MealRecipeItemEntity(
    val mealId: String,
    val recipeId: String,
    val quantity: Double
)