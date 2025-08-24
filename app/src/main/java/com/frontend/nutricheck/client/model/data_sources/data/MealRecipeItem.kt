package com.frontend.nutricheck.client.model.data_sources.data

data class MealRecipeItem(
    override val mealId: String,
    val recipe: Recipe,
    override val quantity: Double = recipe.servings,
    override val servings: Double = recipe.servings,
) : MealItem
