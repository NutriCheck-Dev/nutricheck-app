package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyCalorieState

@Composable
fun CaloriesToday(
    modifier: Modifier = Modifier,
    state: DailyCalorieState,
) {
    val dailyCalories = state.dailyCalories
    val calorieGoal = state.calorieGoal
    val progress = (dailyCalories.toFloat() / calorieGoal).coerceIn(0f, 1f)

    Surface(
        color = Color(0xff121212),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .shadow(6.dp, RoundedCornerShape(16.dp))
    ) {
        Box(modifier = Modifier.requiredSize(193.dp)) {
            // Text: "Heute"
            Text(
                text = stringResource(id = R.string.homepage_day_description),
                color = Color.White,
                lineHeight = 1.5.em,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(15.dp, 15.dp)
            )

            // Kreis + Werte
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(34.dp, 34.dp)
                    .size(125.dp),
                contentAlignment = Alignment.Center
            ) {
                // Fortschritt (Kreisfüllung)
                Canvas(modifier = Modifier.matchParentSize()) {
                    val sweepAngle = progress * 360f
                    val stroke = Stroke(width = 25f, cap = StrokeCap.Round)
                    drawArc(
                        color = Color(0xFFBEB5B5),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = stroke
                    )
                    drawArc(
                        color = Color(0xff4580ff),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = stroke
                    )
                }


                Text(
                    text = dailyCalories.toString(),
                    color = Color.White,
                    lineHeight = 1.22.em,
                    modifier = Modifier.offset(y = (-4).dp),
                    fontSize = 36.sp
                )


                // Text "kcal"
                Text(
                    text = "kcal",
                    color = Color.White,
                    lineHeight = 1.43.em,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(49.dp, 77.dp)
                )
            }
        }
    }
}
