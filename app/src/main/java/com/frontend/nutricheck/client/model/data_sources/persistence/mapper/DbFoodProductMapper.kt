package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity

object DbFoodProductMapper {

    fun toFoodProductEntity(foodProduct: FoodProduct): FoodProductEntity =
        FoodProductEntity(
            id = foodProduct.id,
            name = foodProduct.name,
            calories = foodProduct.calories,
            carbohydrates = foodProduct.carbohydrates,
            protein = foodProduct.protein,
            fat = foodProduct.fat,
            servings = foodProduct.servings.toDouble(),
            servingSize = foodProduct.servingSize,
        )

    fun toFoodProduct(foodProductEntity: FoodProductEntity): FoodProduct =
        FoodProduct(
            id = foodProductEntity.id,
            name = foodProductEntity.name,
            calories = foodProductEntity.calories,
            carbohydrates = foodProductEntity.carbohydrates,
            protein = foodProductEntity.protein,
            fat = foodProductEntity.fat,
            servings = foodProductEntity.servings.toInt(),
            servingSize = foodProductEntity.servingSize
        )
}