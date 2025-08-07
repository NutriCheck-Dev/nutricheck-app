package com.frontend.nutricheck.client.model.data_sources.data

import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

data class Ingredient(
    val recipeId: String,
    val foodProduct: FoodProduct,
    val quantity: Double = foodProduct.servings * (foodProduct.servingSize.getAmount() / 100),
    val servings: Int = foodProduct.servings,
    val servingSize: ServingSize = foodProduct.servingSize
)