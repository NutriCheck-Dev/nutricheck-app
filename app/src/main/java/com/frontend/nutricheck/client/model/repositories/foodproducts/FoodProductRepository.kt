package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import kotlinx.coroutines.flow.Flow

interface FoodProductRepository {
    suspend fun searchFoodProduct(foodProductName: String): List<FoodProduct>
    fun getFoodProductById(foodProductId: String): Flow<FoodProduct>
}