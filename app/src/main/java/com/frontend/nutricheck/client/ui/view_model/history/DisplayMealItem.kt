package com.frontend.nutricheck.client.ui.view_model.history

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll

sealed interface DisplayMealItem {
    val name: String
    val quantity: Double
    val calories: Double
    val carbohydrates: Double
    val protein: Double
    val fat: Double
}

data class DisplayMealFoodItem(
    val item: MealFoodItemEntity,
    val product: FoodProductEntity
) : DisplayMealItem {
    override val name get() = product.name
    override val quantity get() = item.quantity
    override val calories get() = product.calories
    override val carbohydrates get() = product.carbohydrates
    override val protein get() = product.protein
    override val fat get() = product.fat
}

data class DisplayMealRecipeItem(
    val item: MealRecipeItemEntity,
    val recipeEntity: RecipeEntity
) : DisplayMealItem {
    override val name get() = recipeEntity.name
    override val quantity get() = item.quantity
    override val calories get() = recipeEntity.calories
    override val carbohydrates get() = recipeEntity.carbohydrates
    override val protein get() = recipeEntity.protein
    override val fat get() = recipeEntity.fat
}

fun buildDisplayMealItems(meals: List<MealWithAll>): List<DisplayMealItem> {
    return meals.flatMap { mealWithAll ->
        // Alle Food-Items
        mealWithAll.mealFoodItems.map { foodItemWithProduct ->
            DisplayMealFoodItem(
                item = foodItemWithProduct.mealFoodItem,
                product = foodItemWithProduct.foodProductEntity
            )
        } +
                // Alle Recipe-Items
                mealWithAll.mealRecipeItems.map { recipeItemWithRecipe ->
                    DisplayMealRecipeItem(
                        item = recipeItemWithRecipe.mealRecipeItem,
                        recipeEntity = recipeItemWithRecipe.recipeEntity
                    )
                }
    }
}
