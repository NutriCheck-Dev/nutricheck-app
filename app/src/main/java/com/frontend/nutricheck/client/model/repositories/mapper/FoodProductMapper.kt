package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

/**
 * Mapper for converting between [FoodProduct] and [FoodProductDTO].
 */
object FoodProductMapper {
    fun toDTO(foodProduct: FoodProduct) : FoodProductDTO = FoodProductDTO(
        id = foodProduct.id,
        name = foodProduct.name,
        calories = foodProduct.calories,
        carbohydrates = foodProduct.carbohydrates,
        protein = foodProduct.protein,
        fat = foodProduct.fat
    )

    fun toData(foodProductDTO: FoodProductDTO): FoodProduct =
        FoodProduct(
            id = foodProductDTO.id,
            name = foodProductDTO.name,
            calories = foodProductDTO.calories,
            carbohydrates = foodProductDTO.carbohydrates,
            protein = foodProductDTO.protein,
            fat = foodProductDTO.fat,
            servings = 1.0,
            servingSize = ServingSize.ONEHOUNDREDGRAMS
        )
}