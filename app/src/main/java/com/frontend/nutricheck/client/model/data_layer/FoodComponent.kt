package com.frontend.nutricheck.client.model.data_layer

interface FoodComponent {
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
    val calories: Int

    /**
     * Returns the protein content of the food component in grams.
     */
    val protein: Int

    /**
     * Returns the carbohydrate content of the food component in grams.
     */
    val carbs: Int

    /**
     * Returns the fat content of the food component in grams.
     */
    val fat: Int
}