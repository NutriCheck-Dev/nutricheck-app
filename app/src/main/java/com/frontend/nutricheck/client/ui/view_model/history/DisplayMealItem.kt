package com.frontend.nutricheck.client.ui.view_model.history

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

sealed interface DisplayMealItem {
    val name: String
    val quantity: Double
    val calories: Double
    val carbohydrates: Double
    val protein: Double
    val fat: Double
}

data class DisplayMealFoodItem(
    val item: MealFoodItem,
    val product: FoodProduct
) : DisplayMealItem {
    override val name get() = product.name
    override val quantity get() = item.quantity
    override val calories get() = product.calories
    override val carbohydrates get() = product.carbohydrates
    override val protein get() = product.protein
    override val fat get() = product.fat
}

data class DisplayMealRecipeItem(
    val item: MealRecipeItem,
    val recipe: Recipe
) : DisplayMealItem {
    override val name get() = recipe.name
    override val quantity get() = item.quantity
    override val calories get() = recipe.calories
    override val carbohydrates get() = recipe.carbohydrates
    override val protein get() = recipe.protein
    override val fat get() = recipe.fat
}
