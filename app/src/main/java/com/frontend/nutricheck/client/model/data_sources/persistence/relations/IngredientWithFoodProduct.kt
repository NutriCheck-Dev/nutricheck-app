package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient

data class IngredientWithFoodProduct(
    @Embedded val ingredient: Ingredient,
    @Relation(
        entity = FoodProduct::class,
        parentColumn = "foodProductId",
        entityColumn = "id"
    )
    val foodProduct: FoodProduct
)