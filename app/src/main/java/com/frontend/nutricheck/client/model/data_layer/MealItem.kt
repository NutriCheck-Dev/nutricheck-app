package com.frontend.nutricheck.client.model.data_layer

interface MealItem {
    val mealId: String
    val quantity: Double
    fun changeQuantity(newQuantity: Double): MealItem {
        return this
    }
}