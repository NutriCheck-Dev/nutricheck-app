package com.frontend.nutricheck.client.model.data_sources.data

import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility

data class Recipe(
    override val id: String = "",
    override val name: String = "",
    override val calories: Double = 0.0,
    override val carbohydrates: Double = 0.0,
    override val protein: Double = 0.0,
    override val fat: Double = 0.0,
    val servings: Int = 1,
    var ingredients: List<Ingredient> = emptyList(),
    val instructions: String = "",
    val visibility: RecipeVisibility = RecipeVisibility.OWNER
) : FoodComponent
