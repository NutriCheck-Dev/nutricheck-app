package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_layer.FoodProduct

interface BaseFoodProductRepository {
    suspend fun getFoodProductsByQuery(query: String): List<FoodProduct>
    suspend fun getFoodProductById(id: String): FoodProduct?
}