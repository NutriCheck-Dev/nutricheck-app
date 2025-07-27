package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,

    @Relation(
        entity = Ingredient::class,
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<IngredientWithFoodProduct>
)