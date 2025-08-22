package com.frontend.nutricheck.client.dto

/**
 * Data Transfer Object for a recipe.
 *
 * @property id The unique identifier for the recipe.
 * @property name The name of the recipe.
 * @property instructions The cooking instructions for the recipe.
 * @property servings The number of servings the recipe yields.
 * @property calories The total calories in the recipe.
 * @property carbohydrates The total carbohydrates in the recipe.
 * @property protein The total protein in the recipe.
 * @property fat The total fat in the recipe.
 * @property ingredients A list of [IngredientDTO] used in the recipe.
 */
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