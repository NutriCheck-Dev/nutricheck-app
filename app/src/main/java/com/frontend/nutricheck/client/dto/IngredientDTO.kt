package com.frontend.nutricheck.client.dto

/**
 * Data transfer object representing an ingredient in a recipe.
 *
 * @property recipeId The unique identifier of the recipe to which this ingredient belongs.
 * @property foodProductId The unique identifier of the food product used as an ingredient.
 * @property foodProduct The [FoodProductDTO] representing the food product used as an ingredient.
 * @property quantity The quantity of the ingredient used in the recipe, typically in grams.
 */
data class IngredientDTO(
    val recipeId: String,
    val foodProductId: String,
    val foodProduct: FoodProductDTO,
    val quantity: Double
)