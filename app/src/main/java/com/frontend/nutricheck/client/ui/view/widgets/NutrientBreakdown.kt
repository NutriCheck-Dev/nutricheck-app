package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.theme.LocalExtendedColors
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyMacrosState

@Composable
fun NutrientBreakdown(
    modifier: Modifier = Modifier,
    dailyMacrosState: DailyMacrosState,
) {
    val protein = dailyMacrosState.dailyProtein
    val proteinGoal = dailyMacrosState.dailyProteinGoal
    val carbs = dailyMacrosState.dailyCarbs
    val carbsGoal = dailyMacrosState.dailyCarbsGoal
    val fat = dailyMacrosState.dailyFat
    val fatGoal = dailyMacrosState.dailyFatGoal

    val colors = MaterialTheme.colorScheme
    Surface(
        color = colors.surfaceContainer,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .shadow(6.dp, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .requiredWidth(186.dp)
                .requiredHeight(193.dp)
        ) {
            Text(
                text = stringResource(R.string.label_macronutrition),
                color = colors.onSurface,
                lineHeight = 1.5.em,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(20.dp, 15.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(20.dp, 49.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MacroProgress(
                    label = stringResource(id = R.string.homepage_nutrition_protein),
                    value = "${protein}g",
                    progress = if (proteinGoal > 0) protein.toFloat() / proteinGoal else 0f
                )
                MacroProgress(
                    label = stringResource(id = R.string.homepage_nutrition_carbs),
                    value = "${carbs}g",
                    progress = if (carbsGoal > 0) carbs.toFloat() / carbsGoal else 0f
                )
                MacroProgress(
                    label = stringResource(id = R.string.homepage_nutrition_fats),
                    value = "${fat}g",
                    progress = if (fatGoal > 0) fat.toFloat() / fatGoal else 0f
                )
            }
        }
    }
}

@Composable
fun MacroProgress(
    label: String,
    value: String,
    progress: Float,
) {
    val colors = MaterialTheme.colorScheme
    val extendedColors = LocalExtendedColors.current

    val backgroundBarColor = colors.surfaceVariant // oder z. B. colors.outlineVariant
    val progressBarColor = extendedColors.chartBlue.color

    Column(
        modifier = Modifier.requiredWidth(150.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                color = colors.onSurface,
                lineHeight = 1.23.em,
            )
            Text(
                text = value,
                color = colors.onSurface,
                textAlign = TextAlign.End,
                lineHeight = 1.23.em,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Hintergrundbalken
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(backgroundBarColor)
        ) {
            // Fortschrittsbalken
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(progressBarColor)
            )
        }
    }
}


