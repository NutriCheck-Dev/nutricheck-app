package com.frontend.nutricheck.client.model.data_sources.data

data class MealFoodItem (
    override val mealId: String = "",
    val foodProductId: String = "",
    override val quantity: Double = 0.0,
) : MealItem
{
    override fun changeQuantity(newQuantity: Double): MealFoodItem {
        return this.copy(quantity = newQuantity)
    }
}