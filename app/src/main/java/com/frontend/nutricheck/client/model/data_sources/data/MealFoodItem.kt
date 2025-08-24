package com.frontend.nutricheck.client.model.data_sources.data

import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

data class MealFoodItem(
    override val mealId: String,
    val foodProduct: FoodProduct,
    override val quantity: Double = foodProduct.servings * (foodProduct.servingSize.getAmount() / 100),
    override val servings: Double = foodProduct.servings,
    val servingSize: ServingSize = foodProduct.servingSize
) : MealItem
