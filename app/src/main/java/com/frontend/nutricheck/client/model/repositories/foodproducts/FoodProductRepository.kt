package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Result
import kotlinx.coroutines.flow.Flow

interface FoodProductRepository {
    suspend fun searchFoodProducts(foodProductName: String, language: String): Flow<Result<List<FoodProduct>>>
    suspend fun getFoodProductById(foodProductId: String): FoodProduct
}