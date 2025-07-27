package com.frontend.nutricheck.client.model.data_sources.data

data class Ingredient(
    val id: String,
    val recipeId: String,
    val foodProduct: FoodProduct,
    val quantity: Double
)