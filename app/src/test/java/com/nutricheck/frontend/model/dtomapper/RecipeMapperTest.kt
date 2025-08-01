package com.nutricheck.frontend.model.dtomapper

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.model.data_sources.data.*
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.repositories.mapper.RecipeMapper
import org.junit.Assert.*
import org.junit.Test

class RecipeMapperTest {

    @Test
    fun `toDto should map Recipe to RecipeDTO correctly`() {
        val ingredient = Ingredient(
            recipeId = "r1",
            foodProduct = FoodProduct(
                id = "f1",
                name = "Haferflocken",
                calories = 370.0,
                carbohydrates = 60.0,
                protein = 13.0,
                fat = 7.0
            ),
            quantity = 2.0
        )

        val recipe = Recipe(
            id = "r1",
            name = "Porridge",
            calories = 300.0,
            carbohydrates = 40.0,
            protein = 10.0,
            fat = 5.0,
            servings = 1,
            instructions = "Kochen.",
            visibility = RecipeVisibility.OWNER,
            ingredients = listOf(ingredient)
        )

        val dto = RecipeMapper.toDto(recipe)

        assertEquals(recipe.id, dto.id)
        assertEquals(recipe.name, dto.name)
        assertEquals(1, dto.ingredients.size)
        assertEquals("Haferflocken", dto.ingredients.first().foodProduct.name)
        assertEquals(RecipeVisibility.OWNER, recipe.visibility)
    }

    @Test
    fun `toData should map RecipeDTO to Recipe with visibility set to PUBLIC`() {
        val ingredientDTO = IngredientDTO(
            recipeId = "r2",
            foodProductId = "f2",
            foodProduct = FoodProductDTO(
                id = "f2",
                name = "Apfel",
                calories = 52.0,
                carbohydrates = 14.0,
                protein = 0.3,
                fat = 0.2
            ),
            quantity = 1.5
        )

        val dto = RecipeDTO(
            id = "r2",
            name = "Apfelkompott",
            instructions = "Schneiden und kochen.",
            servings = 2,
            calories = 120.0,
            carbohydrates = 30.0,
            protein = 1.0,
            fat = 0.5,
            ingredients = listOf(ingredientDTO)
        )

        val result = RecipeMapper.toData(dto)

        assertEquals("r2", result.id)
        assertEquals("Apfelkompott", result.name)
        assertEquals(RecipeVisibility.PUBLIC, result.visibility)
        assertEquals(1, result.ingredients.size)
        assertEquals("Apfel", result.ingredients.first().foodProduct.name)
    }
}
