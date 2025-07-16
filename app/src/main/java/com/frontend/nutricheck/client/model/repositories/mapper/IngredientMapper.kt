package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.FoodComponentDTO
import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

object IngredientMapper {
    fun toDTO(ingredient: Ingredient) : IngredientDTO {
        val componentDto: FoodComponentDTO = when (val component = ingredient.foodComponent) {
            is FoodProduct -> FoodProductMapper.toDTO(component)
            is Recipe -> RecipeMapper.toDto(component)
        }

        return IngredientDTO(
            recipeId = ingredient.recipeId,
            foodComponentId = ingredient.foodProductId,
            foodComponent = componentDto,
            quantity = ingredient.quantity
        )
    }


    fun toEntity(ingredientDTO: IngredientDTO) : Ingredient {
        val componentEntity: FoodComponent = when (val componentDto = ingredientDTO.foodComponent) {
            is FoodProductDTO -> FoodProductMapper.toEntity(componentDto)
            is RecipeDTO -> RecipeMapper.toEntity(componentDto)
        }
        return Ingredient(
            recipeId = ingredientDTO.recipeId,
            foodProductId = ingredientDTO.foodComponentId,
            foodComponent = componentEntity,
            quantity = ingredientDTO.quantity
        )
    }
    fun toEntityList(ingredientDTOs: List<IngredientDTO>): List<Ingredient> =
        ingredientDTOs.map { toEntity(it) }
    fun toDTOList(ingredients: List<Ingredient>): List<IngredientDTO> =
        ingredients.map { toDTO(it) }
}