package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct

interface FoodProductRepository {
    suspend fun searchFoodProduct(foodProductName: String): List<FoodProduct>
}