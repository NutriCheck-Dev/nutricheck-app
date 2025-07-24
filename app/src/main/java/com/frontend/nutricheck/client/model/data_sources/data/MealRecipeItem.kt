package com.frontend.nutricheck.client.model.data_sources.data

data class MealRecipeItem (
    override val mealId: String = "",
    val recipeId: String = "",
    override val quantity: Double = 0.0,
) : MealItem
{
    override fun changeQuantity(newQuantity: Double): MealRecipeItem {
        return this.copy(quantity = newQuantity)
    }
}