package com.frontend.nutricheck.client.model.data_sources.data

sealed interface FoodComponent {
    /**
     * Returns the id of the food component
     */
    val id: String

    /**
     * Returns the name of the food component.
     */
    val name: String

    /**
     * Returns the energy content of the food component in kilocalories.
     */
    val calories: Double

    /**
     * Returns the carbohydrate content of the food component in grams.
     */
    val carbohydrates: Double

    /**
     * Returns the protein content of the food component in grams.
     */
    val protein: Double

    /**
     * Returns the fat content of the food component in grams.
     */
    val fat: Double

    /**
     * Returns the servings of the food component
     */
    val servings: Double
}