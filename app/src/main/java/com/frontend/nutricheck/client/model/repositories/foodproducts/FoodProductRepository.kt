package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing food products.
 */
interface FoodProductRepository {

    /**
     * Searches for food products by name in the specified language using the API.
     */
    suspend fun searchFoodProducts(foodProductName: String, language: String): Flow<Result<List<FoodProduct>>>
    /**
     * Retrieves a food product by its ID.
     */
    suspend fun getFoodProductById(foodProductId: String): FoodProduct
}