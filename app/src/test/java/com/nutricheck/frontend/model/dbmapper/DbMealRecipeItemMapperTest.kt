package com.nutricheck.frontend.model.dbmapper

import com.frontend.nutricheck.client.model.data_sources.data.*
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.*
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealRecipeItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.*
import org.junit.Assert.assertEquals
import org.junit.Test

class DbMealRecipeItemMapperTest {

    @Test
    fun `toMealRecipeItemEntity should map correctly`() {
        val recipe = Recipe(
            id = "recipe123",
            name = "Nudeln mit Soße",
            calories = 450.0,
            carbohydrates = 60.0,
            protein = 15.0,
            fat = 10.0,
            ingredients = emptyList()
        )

        val domain = MealRecipeItem(
            mealId = "meal001",
            recipe = recipe,
            quantity = 1.5
        )

        val entity = DbMealRecipeItemMapper.toMealRecipeItemEntity(domain)

        assertEquals(domain.mealId, entity.mealId)
        assertEquals(domain.recipe.id, entity.recipeId)
        assertEquals(domain.quantity, entity.quantity, 0.001)
    }

    @Test
    fun `toMealRecipeItem should map correctly`() {
        val recipeEntity = RecipeEntity(
            id = "recipe456",
            name = "Pfannkuchen",
            calories = 300.0,
            carbohydrates = 40.0,
            protein = 10.0,
            fat = 8.0
        )

        val ingredientEntity = IngredientEntity(
            recipeId = "recipe456",
            foodProductId = "food001",
            quantity = 2.0
        )

        val foodProductEntity = FoodProductEntity(
            id = "food001",
            name = "Mehl",
            calories = 350.0,
            carbohydrates = 70.0,
            protein = 10.0,
            fat = 1.0
        )

        val recipeWithIngredients = RecipeWithIngredients(
            recipe = recipeEntity,
            ingredients = listOf(
                IngredientWithFoodProduct(
                    ingredient = ingredientEntity,
                    foodProduct = foodProductEntity
                )
            )
        )

        val mealRecipeItemEntity = MealRecipeItemEntity(
            mealId = "meal002",
            recipeId = "recipe456",
            quantity = 2.0
        )

        val relation = MealRecipeItemWithRecipe(
            mealRecipeItem = mealRecipeItemEntity,
            recipeWithIngredients = recipeWithIngredients
        )

        val result = DbMealRecipeItemMapper.toMealRecipeItem(relation)

        assertEquals("meal002", result.mealId)
        assertEquals("recipe456", result.recipe.id)
        assertEquals("Mehl", result.recipe.ingredients.first().foodProduct.name)
        assertEquals(2.0, result.quantity, 0.001)
    }
}
