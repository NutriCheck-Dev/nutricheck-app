package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealFoodItemWithProduct

/**
 * Mapper for converting between [MealFoodItem] and [MealFoodItemEntity].
 */
object DbMealFoodItemMapper {

    fun toMealFoodItemEntity(mealFoodItem: MealFoodItem) : MealFoodItemEntity =
        MealFoodItemEntity(
            mealId = mealFoodItem.mealId,
            foodProductId = mealFoodItem.foodProduct.id,
            quantity = mealFoodItem.quantity,
            servings = mealFoodItem.servings,
            servingSize = mealFoodItem.servingSize
        )

    fun toMealFoodItem(mealFoodItemWithProduct: MealFoodItemWithProduct) : MealFoodItem =
        MealFoodItem(
            mealId = mealFoodItemWithProduct.mealFoodItem.mealId,
            foodProduct = DbFoodProductMapper.toFoodProduct(mealFoodItemWithProduct.foodProduct),
            quantity = mealFoodItemWithProduct.mealFoodItem.quantity,
            servings = mealFoodItemWithProduct.mealFoodItem.servings,
            servingSize = mealFoodItemWithProduct.mealFoodItem.servingSize
        )
}