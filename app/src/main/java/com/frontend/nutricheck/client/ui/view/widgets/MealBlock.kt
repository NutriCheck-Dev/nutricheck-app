package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
            )
    }
}

//This file represents a meal block that is used in the History Page
@Composable
fun MealBlock(
    modifier: Modifier = Modifier,
    mealName: String,
    calories: Double,
    addOnClick: () -> Unit = {},
    optionsOnClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF121212))
    ) {
        MealHeader(mealName, calorieCount = calories, modifier = Modifier.padding(horizontal = 16.dp))
        HorizontalDivider(
            color = Color(0xFFFFFFFF),
            thickness = 1.dp
        )
        DishItemMealButton()
        HorizontalDivider(
            color = Color(0xFFFFFFFF),
            thickness = 1.dp
        )
        MealFooter()
    }
}

@Preview
@Composable
fun MealFooterPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF121212))
    ) {
        MealHeader("XD", calorieCount = 200.0)

        //Gericht & Divider zusammen, sodass es keinen doppelten Divider gibt
        HorizontalDivider(
            color = Color(0xFFFFFFFF),
            thickness = 1.dp
        )
        MealFooter()
    }

}