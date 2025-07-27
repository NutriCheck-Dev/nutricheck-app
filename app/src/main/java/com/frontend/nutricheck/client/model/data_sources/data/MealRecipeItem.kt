package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity

@Entity(
    tableName = "meal_recipe_items",
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mealId"]),
        Index(value = ["recipeId"])
    ]
)
data class MealRecipeItem(
    @PrimaryKey override val id: String = "",
    override val mealId: String = "",
    val recipeId: String = "",
    override val quantity: Double = 0.0
) : MealItem