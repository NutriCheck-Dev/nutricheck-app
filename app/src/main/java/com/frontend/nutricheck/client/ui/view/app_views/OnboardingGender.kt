package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingGender(
    modifier: Modifier = Modifier,
    question: String = "Als was identifizierst du dich?",
    options: List<String> = listOf("Männlich", "Weiblich", "Divers"),
    onOptionClick: (String) -> Unit = {},
    onNextClick: () -> Unit = {}
) {}