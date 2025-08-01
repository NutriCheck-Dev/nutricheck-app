package com.nutricheck.frontend.model.dbmapper

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import org.junit.Assert.assertEquals
import org.junit.Test

class DbRecipeMapperTest {

    @Test
    fun `toRecipeEntity should map Recipe to RecipeEntity correctly`() {
        val recipe = Recipe(
            id = "r001",
            name = "Quarkauflauf",
            calories = 250.0,
            carbohydrates = 20.0,
            protein = 15.0,
            fat = 8.0,
            servings = 4,
            instructions = "Alles vermengen und backen.",
            visibility = RecipeVisibility.PUBLIC,
            ingredients = emptyList()
        )

        val entity = DbRecipeMapper.toRecipeEntity(recipe)

        assertEquals(recipe.id, entity.id)
        assertEquals(recipe.name, entity.name)
        assertEquals(recipe.calories, entity.calories, 0.001)
        assertEquals(recipe.servings.toDouble(), entity.servings, 0.001)
        assertEquals(recipe.instructions, entity.instructions)
        assertEquals(recipe.visibility, entity.visibility)
    }

    @Test
    fun `toRecipe should map RecipeWithIngredients to Recipe correctly`() {
        val foodEntity = FoodProductEntity(
            id = "f123",
            name = "Haferflocken",
            calories = 370.0,
            carbohydrates = 60.0,
            protein = 13.0,
            fat = 7.0
        )

        val ingredientEntity = IngredientEntity(
            recipeId = "r002",
            foodProductId = "f123",
            quantity = 2.0
        )

        val ingredientWithFoodProduct = IngredientWithFoodProduct(
            ingredient = ingredientEntity,
            foodProduct = foodEntity
        )

        val recipeEntity = RecipeEntity(
            id = "r002",
            name = "Porridge",
            calories = 300.0,
            carbohydrates = 45.0,
            protein = 10.0,
            fat = 5.0,
            servings = 2.0,
            instructions = "Kochen und rühren.",
            visibility = RecipeVisibility.OWNER
        )

        val recipeWithIngredients = RecipeWithIngredients(
            recipe = recipeEntity,
            ingredients = listOf(ingredientWithFoodProduct)
        )

        val domain = DbRecipeMapper.toRecipe(recipeWithIngredients)

        assertEquals("r002", domain.id)
        assertEquals("Porridge", domain.name)
        assertEquals(1, domain.ingredients.size)
        assertEquals("Haferflocken", domain.ingredients.first().foodProduct.name)
        assertEquals(2, domain.servings)
    }
}
