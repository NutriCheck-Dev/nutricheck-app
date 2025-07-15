package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

@Composable
fun MealHeader(
    titel: String,
    modifier: Modifier = Modifier,
    calorieCount: Double
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFF121212)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = titel,
            color = Color(0xFFFFFFFF),
            lineHeight = 16.sp,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
            )
        Text(
            text = "$calorieCount",
            color = Color(0xFFFFFFFF),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
            )
    }
}

@Composable
fun MealFooter(
    modifier: Modifier = Modifier,
    text: String = "+ Hinzufügen",
    onMoreClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(
            text = "+ Hinzufügen",
            color = Color(0xFF4580FF),
            lineHeight = 16.sp,
            fontSize = 12.sp,
            modifier = Modifier.clickable(onClick = onMoreClick),
            )
    }
}

//This file represents a meal block that is used in the History Page
@Composable
fun MealBlock(
    modifier: Modifier = Modifier,
    mealName: String,
    totalCalories: Double,
    meals: List<MealItem>,
    addOnClick: () -> Unit = {},
    optionsOnClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF121212))
    ) {
        MealHeader(mealName, calorieCount = totalCalories, modifier = Modifier.padding(horizontal = 16.dp))
        HorizontalDivider(
            color = Color(0xFFFFFFFF),
            thickness = 1.dp
        )
        meals.forEach { mealItem ->
            val (name, calories, quantity) = when (mealItem) {
                is MealFoodItem -> {
                    val name = mealItem.foodProductId // Falls du FoodProduct laden willst, hier ersetzen
                    val kcal = 0.0
                    Triple(name, kcal, mealItem.quantity)
                }
                is MealRecipeItem -> {
                    val name = mealItem.recipe?.name ?: "Unbekannt"
                    val kcal = mealItem.recipe?.calories ?: 0.0
                    Triple(name, kcal, mealItem.quantity)
                }
                else -> Triple("Unbekannt", 0.0, 0.0)
            }

            DishItemMealButton(
                title = name,
                calories = calories,
                quantity = quantity
            )

            HorizontalDivider(color = Color(0xFFFFFFFF), thickness = 1.dp)
        }
        MealFooter()
    }
}

@Preview
@Composable
fun MealFooterPreview() {
    MealBlock(modifier = Modifier.padding(7.dp), "Frühstück", 300.0, meals= listOf(MealRecipeItem(
        mealId = "1",
        quantity = 1.0,
        recipeId = "recipe1",
        recipe = Recipe(
            id = "recipe1",
            name = "Oatmeal",
            description = "Healthy oatmeal with fruits",
            ingredients = setOf(Ingredient(
                recipeId = "ingredient1",
                id = "Oats",
                quantity = 100.0,
                foodProduct = FoodProduct()
            )),
            calories = 300.0,
        ))))

}