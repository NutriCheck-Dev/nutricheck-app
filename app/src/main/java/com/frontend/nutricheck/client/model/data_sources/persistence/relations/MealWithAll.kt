package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity

data class MealWithAll(
    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealId",
        entity = MealFoodItemEntity::class
    )
    val mealFoodItems: List<MealFoodItemWithProduct>,

    @Relation(
        parentColumn = "id",
        entityColumn = "mealId",
        entity = MealRecipeItemEntity::class
    )
    val mealRecipeItems: List<MealRecipeItemWithRecipe>
)

// 1. FoodItem + Product
data class MealFoodItemWithProduct(
    @Embedded val mealFoodItem: MealFoodItemEntity,

    @Relation(
        parentColumn = "foodProductId",
        entityColumn = "id"
    )
    val foodProduct: FoodProductEntity
)

// 2. RecipeItem + Recipe (+ Ingredients + FoodProducts)
data class MealRecipeItemWithRecipe(
    @Embedded val mealRecipeItem: MealRecipeItemEntity,

    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id",
        entity = RecipeEntity::class
    )
    val recipeWithIngredients: RecipeWithIngredients
)

// 3. Rezept + alle Ingredients (mit ihren FoodProducts)
data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = IngredientEntity::class
    )
    val ingredients: List<IngredientWithFoodProduct>
)

// 4. Ingredient + zugehöriges FoodProduct
data class IngredientWithFoodProduct(
    @Embedded val ingredient: IngredientEntity,

    @Relation(
        parentColumn = "foodProductId",
        entityColumn = "id"
    )
    val foodProduct: FoodProductEntity
)

