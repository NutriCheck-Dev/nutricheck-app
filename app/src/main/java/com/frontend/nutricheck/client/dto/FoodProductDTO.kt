package com.frontend.nutricheck.client.dto
/**
 * Data transfer object representing a food product.
 *
 * @property id The unique identifier of the food product.
 * @property name The name of the food product.
 * @property calories The number of calories in the food product.
 * @property carbohydrates The amount of carbohydrates in the food product (in grams).
 * @property protein The amount of protein in the food product (in grams).
 * @property fat The amount of fat in the food product (in grams).
 *
 */
data class FoodProductDTO(
    val id: String,
    val name: String,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double
)