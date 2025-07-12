package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao

class FoodProductRepositoryImpl(val foodDao: FoodDao) : FoodProductRepository {
    override suspend fun getFoodProductsByQuery(query: String): List<FoodProduct> {
        // TODO: Implementiere die Logik, um FoodProducts anhand des query-Strings zu suchen
        return emptyList()
    }

    override suspend fun getFoodProductById(id: String): FoodProduct? {
        // TODO: Implementiere die Logik, um ein FoodProduct anhand der ID zu finden
        return null
    }
}