package com.frontend.nutricheck.client.dto

/**
 * Data transfer object representing a meal.
 * @property calories The total number of calories in the meal.
 * @property carbohydrates The total amount of carbohydrates in the meal (in grams).
 * @property protein The total amount of protein in the meal (in grams).
 * @property fat The total amount of fat in the meal (in grams).
 * @property items A set of [MealItemDTO] representing the individual items in the meal
 */
data class MealDTO(
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val items: Set<MealItemDTO>
)