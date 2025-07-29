package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealRecipeItemWithRecipe

object DbMealRecipeItemMapper {

    fun toMealRecipeItemEntity(mealRecipeItem: MealRecipeItem) : MealRecipeItemEntity =
        MealRecipeItemEntity(
            mealId = mealRecipeItem.mealId,
            recipeId = mealRecipeItem.recipe.id,
            quantity = mealRecipeItem.quantity
        )

    fun toMealRecipeItem(mealRecipeItemWithRecipe: MealRecipeItemWithRecipe) : MealRecipeItem =
        MealRecipeItem(
            mealId = mealRecipeItemWithRecipe.mealRecipeItem.mealId,
            recipe = DbRecipeMapper.toRecipe(mealRecipeItemWithRecipe.recipeWithIngredients),
            quantity = mealRecipeItemWithRecipe.mealRecipeItem.quantity
        )
}