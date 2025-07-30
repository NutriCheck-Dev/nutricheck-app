package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealFoodItemWithProduct

object DbMealFoodItemMapper {

    fun toMealFoodItemEntity(mealFoodItem: MealFoodItem) : MealFoodItemEntity =
        MealFoodItemEntity(
            mealId = mealFoodItem.mealId,
            foodProductId = mealFoodItem.foodProduct.id,
            quantity = mealFoodItem.quantity
        )

    fun toMealFoodItem(mealFoodItemWithProduct: MealFoodItemWithProduct) : MealFoodItem =
        MealFoodItem(
            mealId = mealFoodItemWithProduct.mealFoodItem.mealId,
            foodProduct = DbFoodProductMapper.toFoodProduct(mealFoodItemWithProduct.foodProduct),
            quantity = mealFoodItemWithProduct.mealFoodItem.quantity
        )
}