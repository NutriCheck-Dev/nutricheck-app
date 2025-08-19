package com.frontend.nutricheck.client.model.repositories.mapper

import com.nutricheck.frontend.TestDataFactory
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class IngredientMapperTest {

    val ingredient = TestDataFactory.createDefaultIngredient()
    val ingredientDTO = TestDataFactory.createDefaultIngredientDTO()

    @Test
    fun `toDTO should convert Ingredient to IngredientDTO`() {
        val foodProductDTO = TestDataFactory.createDefaultFoodProductDTO()
        mockkObject(FoodProductMapper)
        every { FoodProductMapper.toDTO(ingredient.foodProduct) } returns foodProductDTO

        val dto = IngredientMapper.toDTO(ingredient)

        assertEquals(ingredient.recipeId, dto.recipeId)
        assertEquals(ingredient.quantity, dto.quantity, 0.0)
        assertEquals(ingredient.foodProduct.id, dto.foodProductId)
        assertEquals(ingredient.foodProduct.fat, dto.foodProduct.fat, 0.0)
        assertEquals(ingredient.foodProduct.protein, dto.foodProduct.protein, 0.0)
        assertEquals(ingredient.foodProduct.carbohydrates, dto.foodProduct.carbohydrates, 0.0)
        assertEquals(ingredient.foodProduct.calories, dto.foodProduct.calories, 0.0)
        assertEquals(foodProductDTO.name, dto.foodProduct.name)

        unmockkObject(FoodProductMapper)
    }

    @Test
    fun `toData should convert IngredientDTO to Ingredient`() {
        val foodProduct = TestDataFactory.createDefaultFoodProduct()
        mockkObject(FoodProductMapper)
        every { FoodProductMapper.toData(ingredientDTO.foodProduct) } returns foodProduct

        val data = IngredientMapper.toData(ingredientDTO)

        assertEquals(ingredientDTO.recipeId, data.recipeId)
        assertEquals(ingredientDTO.quantity, data.quantity, 0.0)
        assertEquals(foodProduct.id, data.foodProduct.id)
        assertEquals(foodProduct.fat, data.foodProduct.fat, 0.0)
        assertEquals(foodProduct.protein, data.foodProduct.protein, 0.0)
        assertEquals(foodProduct.carbohydrates, data.foodProduct.carbohydrates, 0.0)
        assertEquals(foodProduct.calories, data.foodProduct.calories, 0.0)

        unmockkObject(FoodProductMapper)
    }
}