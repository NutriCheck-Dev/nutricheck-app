package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.MealItemDTO
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity

object MealItemMapper {
    fun toEntity(mealItemDto: MealItemDTO): MealFoodItemEntity {
        TODO("Implement the conversion from MealItemDTO to MealFoodItem")
    }
    fun toEntityList(mealItemDtos: List<MealItemDTO>): List<MealFoodItemEntity> {
        return mealItemDtos.map { toEntity(it) }
    }
}