package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct

object FoodProductMapper {
    fun toDTO(foodProduct: FoodProduct) : FoodProductDTO = FoodProductDTO(
        id = foodProduct.id,
        name = foodProduct.name,
        calories = foodProduct.calories,
        carbohydrates = foodProduct.carbohydrates,
        protein = foodProduct.protein,
        fat = foodProduct.fat
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