package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct

interface FoodProductRepository {
    suspend fun getFoodProductsByQuery(query: String): List<FoodProduct>
    suspend fun getFoodProductById(id: String): FoodProduct?
}