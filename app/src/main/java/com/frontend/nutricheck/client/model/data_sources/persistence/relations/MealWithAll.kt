package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity

data class MealWithAll(
    @Embedded val meal: MealEntity,
    @Relation(
        entity = MealFoodItemEntity::class,
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val mealFoodItems: List<MealFoodItemWithProduct>,
    @Relation(
        entity = MealRecipeItemEntity::class,
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val mealRecipeItems: List<MealRecipeItemWithRecipe>
)

data class MealFoodItemWithProduct(
    @Embedded val mealFoodItemEntity: MealFoodItemEntity,
    @Relation(
        parentColumn = "foodProductId",
        entityColumn = "id"
    )
    val foodProductEntity: FoodProductEntity
)

data class MealRecipeItemWithRecipe(
    @Embedded val mealRecipeItemEntity: MealRecipeItemEntity,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipeWithIngredients: RecipeWithIngredients
)
