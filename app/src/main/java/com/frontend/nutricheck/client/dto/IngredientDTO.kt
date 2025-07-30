package com.frontend.nutricheck.client.dto

data class IngredientDTO(
    val recipeId: String,
    val foodProductId: String,
    val foodProduct: FoodProductDTO,
    val quantity: Double
)