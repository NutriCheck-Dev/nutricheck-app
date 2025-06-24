package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingSport(
    modifier: Modifier = Modifier,
    question: String = "Wie oft treibst du Sport?",
    options: List<String> = listOf("5+/Woche", "3-4/Woche", "1-2/Woche", "Nie"),
    onOptionClick: (String) -> Unit = {},
    onNextClick: () -> Unit = {}
) {}