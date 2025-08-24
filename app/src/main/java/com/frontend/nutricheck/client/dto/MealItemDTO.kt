package com.frontend.nutricheck.client.dto

/**
 * Data Transfer Object for a meal item.
 *
 * @property foodProductId The unique identifier for the food product.
 * @property foodProduct The details of the [FoodProductDTO].
 */
data class MealItemDTO(
    val foodProductId: String,
    val foodProduct: FoodProductDTO
)