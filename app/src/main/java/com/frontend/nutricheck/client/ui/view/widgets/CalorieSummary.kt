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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel

// This file defines a composable function for displaying a calorie summary widget.
@Composable
fun CalorieSummary(
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    title: String = "Verbleibende Kalorien",
    goalCalories: Int = 2000,
    consumedCalories: Int = 1500,
    remainingCalories: Int = 500,
    onClick: () -> Unit = {}
) {
    Surface(
        color = Color(0xFF121212),
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()
        .background(Color(0xFF121212))) {

            // Titel oben links
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFFFFFFF),
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 10.dp)
            )

            // Drei Werte + Symbole zentriert mit je 46dp Abstand links/rechts
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 46.dp, end = 46.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CalorieInfo(value = goalCalories, label = "Ziel")

                MinusEqualsSymbol("-")

                CalorieInfo(value = consumedCalories, label = "Essen")

                MinusEqualsSymbol("=")

                CalorieInfo(value = remainingCalories, label = "Verbleibend")
            }
        }
    }
}

@Composable
fun CalorieInfo(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = TextStyle(
                fontSize = 10.sp,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun MinusEqualsSymbol(symbol: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (symbol == "-") {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(1.dp)
                    .background(Color(0xFFFFFFFF))
            )
        } else if (symbol == "=") {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(1.dp)
                    .background(Color(0xFFFFFFFF))
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(1.dp)
                    .background(Color(0xFFFFFFFF))
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CalorieSummaryPreview() {
    CalorieSummary(
        historyViewModel = hiltViewModel(),
        title = "Verbleibende Kalorien",
        goalCalories = 2000,
        consumedCalories = 1500,
        remainingCalories = 500,
        onClick = {}
    )
}