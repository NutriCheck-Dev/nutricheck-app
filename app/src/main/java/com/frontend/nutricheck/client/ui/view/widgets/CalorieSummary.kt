package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.ui.view_model.HistoryState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.frontend.nutricheck.client.R

@Composable
fun CalorieSummary(
    modifier: Modifier = Modifier,
    state: HistoryState
) {
    val goalCalories = state.goalCalories
    val consumedCalories = state.totalCalories
    val remainingCalories = goalCalories - consumedCalories

    val colors = MaterialTheme.colorScheme

    Surface(
        color = colors.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surfaceContainer)
        ) {
            Text(
                text = stringResource(R.string.label_calories_remaining),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.onSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 10.dp)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 46.dp, end = 46.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CalorieInfo(value = goalCalories, label = stringResource(R.string.label_calorie_goal), textColor = colors.onSurfaceVariant)
                MinusEqualsSymbol("-", color = colors.onSurfaceVariant)
                CalorieInfo(value = consumedCalories, label = stringResource(R.string.label_calories_consumed), textColor = colors.onSurfaceVariant)
                MinusEqualsSymbol("=", color = colors.onSurfaceVariant)
                CalorieInfo(value = remainingCalories, label = stringResource(R.string.label_remaining), textColor = colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CalorieInfo(value: Int, label: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = TextStyle(
                fontSize = 14.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = TextStyle(
                fontSize = 10.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun MinusEqualsSymbol(symbol: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (symbol == "-") {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(1.dp)
                    .background(color)
            )
        } else if (symbol == "=") {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(1.dp)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(1.dp)
                    .background(color)
            )
        }
    }
}