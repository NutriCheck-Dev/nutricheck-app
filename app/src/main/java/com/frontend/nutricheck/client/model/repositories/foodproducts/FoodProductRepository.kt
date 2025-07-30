package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Result

interface FoodProductRepository {
    suspend fun searchFoodProduct(foodProductName: String, language: String): Result<List<FoodProduct>>
    suspend fun getFoodProductById(foodProductId: String): FoodProduct
}