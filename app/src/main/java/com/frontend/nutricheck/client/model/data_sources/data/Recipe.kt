package com.frontend.nutricheck.client.model.data_sources.data

import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility

data class Recipe(
    override val id: String,
    override val name: String,
    override val calories: Double,
    override val carbohydrates: Double,
    override val protein: Double,
    override val fat: Double,
    val servings: Int,
    val ingredients: List<Ingredient>,
    val instructions: String,
    val visibility: RecipeVisibility
) : FoodComponent
