package com.frontend.nutricheck.client.dto

data class RecipeDTO(
    val id: String,
    val name: String,
    val instructions: String,
    val servings: Int,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val ingredients: List<IngredientDTO>,
)