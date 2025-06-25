package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun OnboardingGender(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    question: String = "Als was identifizierst du dich?",
    options: List<String> = listOf("Männlich", "Weiblich", "Divers"),
    onOptionClick: (String) -> Unit = {},
    onNextClick: () -> Unit = {}
) {}