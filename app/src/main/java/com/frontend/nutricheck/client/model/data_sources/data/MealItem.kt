package com.frontend.nutricheck.client.model.data_sources.data

/**
 * Represents a meal item in the application.
 */
interface MealItem {
    /**
     * Returns the unique identifier of the meal item.
     */
    val mealId: String
    /**
     * Returns the quantity of the meal item.
     */
    val quantity: Double
    /**
     * Returns the servings of the meal item.
     */
    val servings: Int
}