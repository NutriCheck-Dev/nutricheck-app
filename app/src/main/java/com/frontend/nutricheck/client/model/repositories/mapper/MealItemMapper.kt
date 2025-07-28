package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.MealItemDTO
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItem

object MealItemMapper {
    fun toEntity(mealItemDto: MealItemDTO): MealFoodItem {
        TODO("Implement the conversion from MealItemDTO to MealFoodItem")
    }
    fun toEntityList(mealItemDtos: List<MealItemDTO>): List<MealFoodItem> {
        return mealItemDtos.map { toEntity(it) }
    }
}