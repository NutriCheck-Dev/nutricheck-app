package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingGoal(
    modifier: Modifier = Modifier,
    question: String = "Welches Ziel verfolgst du?",
    options: List<String> = listOf("Zunehmen", "Abnehmen", "Gewicht halten"),
    onOptionClick: (String) -> Unit = {},
    onNextClick: () -> Unit = {}
) {}