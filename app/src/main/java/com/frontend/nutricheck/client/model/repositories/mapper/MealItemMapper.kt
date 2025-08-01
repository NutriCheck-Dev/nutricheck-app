package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.MealItemDTO
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem

object MealItemMapper {
    fun toData(mealItemDTO: MealItemDTO, mealId: String): MealFoodItem =
        MealFoodItem(
            mealId = mealId,
            foodProduct = FoodProductMapper.toData(mealItemDTO.foodProduct),
            quantity = 1.0
        )
}