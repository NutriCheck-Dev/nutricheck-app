package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity

/**
 * Represents a Meal with all its related entities
 */
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

/**
 * Represents a single MealFoodItem with its associated FoodProduct
 */
data class MealFoodItemWithProduct(
    @Embedded val mealFoodItem: MealFoodItemEntity,

    @Relation(
        parentColumn = "foodProductId",
        entityColumn = "id"
    )
    val foodProduct: FoodProductEntity
)

/**
 * Represents a single MealRecipeItem with its associated Recipe (including Ingredients and FoodProducts)
 */
data class MealRecipeItemWithRecipe(
    @Embedded val mealRecipeItem: MealRecipeItemEntity,

    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id",
        entity = RecipeEntity::class
    )
    val recipeWithIngredients: RecipeWithIngredients
)

/**
 * Represents a Recipe with its associated Ingredients (including FoodProducts)
 */
data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = IngredientEntity::class
    )
    val ingredients: List<IngredientWithFoodProduct>
)

/**
 * Represents an Ingredient with its associated FoodProduct
 */
data class IngredientWithFoodProduct(
    @Embedded val ingredient: IngredientEntity,

    @Relation(
        parentColumn = "foodProductId",
        entityColumn = "id"
    )
    val foodProduct: FoodProductEntity
)

