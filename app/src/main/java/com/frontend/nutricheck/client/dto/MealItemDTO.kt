package com.frontend.nutricheck.client.dto


data class MealItemDTO(
    val mealId: String?,
    val foodProductId: String?,
    val foodProduct: FoodProductDTO?
)