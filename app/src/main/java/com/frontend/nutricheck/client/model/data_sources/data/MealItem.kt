package com.frontend.nutricheck.client.model.data_sources.data

interface MealItem {
    val mealId: String
    val quantity: Double
    fun changeQuantity(newQuantity: Double): MealItem {
        return this
    }
}