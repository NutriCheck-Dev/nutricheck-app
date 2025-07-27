package com.frontend.nutricheck.client.dto

import com.frontend.nutricheck.client.model.data_sources.data.RecipeVisibility

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
    val visibility: RecipeVisibility
)