package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import org.junit.Test
import org.junit.Assert.assertEquals

class DbRecipeMapperTest {

    val recipe = TestDataFactory.createDefaultRecipe()
    val recipeEntity = TestDataFactory.createDefaultRecipeEntity()

    @Test
    fun `toEntity should convert Recipe to RecipeEntity`() {
        val entity = DbRecipeMapper.toRecipeEntity(recipe, delete = false)
        assertEquals(recipeEntity.id, entity.id)
        assertEquals(recipeEntity.name, entity.name)
        assertEquals(recipeEntity.calories, entity.calories, 0.0)
        assertEquals(recipeEntity.carbohydrates, entity.carbohydrates, 0.0)
        assertEquals(recipeEntity.protein, entity.protein, 0.0)
        assertEquals(recipeEntity.fat, entity.fat, 0.0)
        assertEquals(recipeEntity.servings, entity.servings, 0.0)
        assertEquals(recipeEntity.instructions, entity.instructions)
        assertEquals(recipeEntity.visibility, entity.visibility)
    }

    @Test
    fun `toRecipe should convert RecipeWithIngredients to Recipe`() {
        val recipeWithIngredients = TestDataFactory.createDefaultRecipeWithIngredients()
        val recipeResult = DbRecipeMapper.toRecipe(recipeWithIngredients)
        assertEquals(recipe.id, recipeResult.id)
        assertEquals(recipe.name, recipeResult.name)
        assertEquals(recipe.calories, recipeResult.calories, 0.0)
        assertEquals(recipe.carbohydrates, recipeResult.carbohydrates, 0.0)
        assertEquals(recipe.protein, recipeResult.protein, 0.0)
        assertEquals(recipe.fat, recipeResult.fat, 0.0)
        assertEquals(recipe.servings, recipeResult.servings, 0.0)
        assertEquals(recipe.instructions, recipeResult.instructions)
        assertEquals(recipe.visibility, recipeResult.visibility)
        assertEquals(recipe.ingredients.first(), recipeResult.ingredients.first())
    }

}