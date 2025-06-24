package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingWeightGoal(
    modifier: Modifier = Modifier,
    question: String = "Welches Zielgewicht hast du?",
    onNextClick: () -> Unit = {}
) {}