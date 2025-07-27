package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct

object FoodProductMapper {
    fun toDTO(foodProductEntity: FoodProduct) : FoodProductDTO = FoodProductDTO(
        id = foodProductEntity.id,
        name = foodProductEntity.name,
        calories = foodProductEntity.calories,
        carbohydrates = foodProductEntity.carbohydrates,
        protein = foodProductEntity.protein,
        fat = foodProductEntity.fat
    )

    fun toEntity(foodProductDTO: FoodProductDTO): FoodProduct =
        FoodProduct(
            id = foodProductDTO.id,
            name = foodProductDTO.name,
            calories = foodProductDTO.calories,
            carbohydrates = foodProductDTO.carbohydrates,
            protein = foodProductDTO.protein,
            fat = foodProductDTO.fat
        )
}