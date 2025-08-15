package com.nutricheck.frontend

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity

object TestDataFactory {

    fun createDefaultFoodProductDTO() = FoodProductDTO(
        id = "testId",
        name = "testName",
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0
    )

    fun createDefaultFoodProduct() = FoodProduct(
        id = "testId",
        name = "testName",
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
        servings =  1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    fun createDefaultFoodProductEntity() = FoodProductEntity(
        id = "testId",
        name = "testName",
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
    )
}