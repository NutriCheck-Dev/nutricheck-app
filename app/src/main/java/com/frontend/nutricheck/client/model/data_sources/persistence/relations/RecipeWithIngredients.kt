package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity

data class RecipeWithIngredients(
    @Embedded val recipeEntity: RecipeEntity,

    @Relation(
        entity = IngredientEntity::class,
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<IngredientWithFoodProduct>
)