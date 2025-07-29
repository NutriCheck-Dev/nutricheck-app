package com.frontend.nutricheck.client.model.data_sources.data

data class Ingredient(
    //val id: String,//?
    val recipeId: String, //just recipeId?? easier for mapper etc.
    val foodProduct: FoodProduct,
    val quantity: Double
)