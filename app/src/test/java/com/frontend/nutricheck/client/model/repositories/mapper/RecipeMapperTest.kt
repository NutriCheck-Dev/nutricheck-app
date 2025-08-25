package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class RecipeMapperTest {

    val recipe = TestDataFactory.createDefaultRecipe()
    val recipeDTO = TestDataFactory.createDefaultRecipeDTO()
    val ingredient = TestDataFactory.createDefaultIngredient()
    val ingredientDTO = TestDataFactory.createDefaultIngredientDTO()

    @Test
    fun `toDTO should convert Recipe to RecipeDTO`() {
        mockkObject(IngredientMapper)
        every { IngredientMapper.toDTO(ingredient) } returns ingredientDTO

        val dto = RecipeMapper.toDto(recipe)

        assertEquals(recipe.id, dto.id)
        assertEquals(recipe.name, dto.name)
        assertEquals(recipe.instructions, dto.instructions)
        assertEquals(recipe.servings, dto.servings.toDouble())
        assertEquals(recipe.calories, dto.calories, 0.0)
        assertEquals(recipe.carbohydrates, dto.carbohydrates, 0.0)
        assertEquals(recipe.protein, dto.protein, 0.0)
        assertEquals(recipe.fat, dto.fat, 0.0)
        assertEquals(recipe.ingredients.first().recipeId, dto.ingredients.first().recipeId)
        assertEquals(recipe.ingredients.first().foodProduct.id, dto.ingredients.first().foodProductId)
        assertEquals(recipe.ingredients.first().quantity, dto.ingredients.first().quantity, 0.0)
    }

    @Test
    fun `toData should convert RecipeDTO to Recipe`() {
        mockkObject(IngredientMapper)
        every { IngredientMapper.toData(ingredientDTO) } returns ingredient

        val data = RecipeMapper.toData(recipeDTO)

        assertEquals(recipeDTO.id, data.id)
        assertEquals(recipeDTO.name, data.name)
        assertEquals(recipeDTO.instructions, data.instructions)
        assertEquals(recipeDTO.servings, data.servings.toInt())
        assertEquals(recipeDTO.calories, data.calories, 0.0)
        assertEquals(recipeDTO.carbohydrates, data.carbohydrates, 0.0)
        assertEquals(recipeDTO.protein, data.protein, 0.0)
        assertEquals(recipeDTO.fat, data.fat, 0.0)
        assertEquals(recipeDTO.ingredients.first().recipeId, data.ingredients.first().recipeId)
        assertEquals(recipeDTO.ingredients.first().foodProductId, data.ingredients.first().foodProduct.id)
        assertEquals(recipeDTO.ingredients.first().quantity, data.ingredients.first().quantity, 0.0)

        unmockkObject(IngredientMapper)
    }
}