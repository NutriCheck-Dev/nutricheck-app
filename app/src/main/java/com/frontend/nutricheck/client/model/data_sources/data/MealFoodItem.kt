package com.frontend.nutricheck.client.model.data_sources.data

data class MealFoodItem(
    override val mealId: String,
    val foodProduct: FoodProduct,
    override val quantity: Double
) : MealItem
