package com.frontend.nutricheck.client.model.data_sources.data

data class Ingredient(
    val recipeId: String,
    val foodProduct: FoodProduct,
    val quantity: Double
)