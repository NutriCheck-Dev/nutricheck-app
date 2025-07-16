package com.frontend.nutricheck.client.dto


sealed interface FoodComponentDTO

data class FoodProductDTO(
    val id: String,
    val name: String,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double
    //val servings: Int
) : FoodComponentDTO

data class RecipeDTO(
    val id: String,
    val name: String,
    val instructions: String,
    val servings: Int,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val ingredients: Set<IngredientDTO>
) : FoodComponentDTO

data class IngredientDTO(
    val recipeId: String,
    val foodComponentId: String,
    val foodComponent: FoodComponentDTO,
    val quantity: Double
)