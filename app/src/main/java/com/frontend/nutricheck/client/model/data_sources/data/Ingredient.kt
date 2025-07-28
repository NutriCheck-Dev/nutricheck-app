package com.frontend.nutricheck.client.model.data_sources.data

data class Ingredient(
    //val id: String,//?
    val recipe: Recipe,
    val foodProduct: FoodProduct,
    val quantity: Double
)