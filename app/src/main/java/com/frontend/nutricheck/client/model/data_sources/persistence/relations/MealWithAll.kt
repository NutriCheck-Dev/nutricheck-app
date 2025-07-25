package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

data class MealWithAll(
    @Embedded val meal: Meal,
    @Relation(
        entity = MealFoodItem::class,
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val mealFoodItems: List<MealFoodItemWithProduct>,
    @Relation(
        entity = MealRecipeItem::class,
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val mealRecipeItems: List<MealRecipeItemWithRecipe>
)

data class MealFoodItemWithProduct(
    @Embedded val mealFoodItem: MealFoodItem,
    @Relation(
        parentColumn = "foodProductId",
        entityColumn = "id"
    )
    val foodProduct: FoodProduct
)

data class MealRecipeItemWithRecipe(
    @Embedded val mealRecipeItem: MealRecipeItem,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipe: Recipe
)
