package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.frontend.nutricheck.client.R



@Composable
fun Onboardingbirthday(
    modifier: Modifier = Modifier,
    question: String = stringResource(id = R.string.onboarding_birthday_question),
    onNextClick: () -> Unit = {}
) {}