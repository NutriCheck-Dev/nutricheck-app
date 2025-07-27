package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity

data class IngredientWithFoodProduct(
    @Embedded val ingredientEntity: IngredientEntity,
    @Relation(
        entity = FoodProductEntity::class,
        parentColumn = "foodProductId",
        entityColumn = "id"
    )
    val foodProductEntity: FoodProductEntity
)