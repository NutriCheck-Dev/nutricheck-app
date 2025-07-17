package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.frontend.nutricheck.client.ui.view_model.onboarding.OnboardingEvent
import com.frontend.nutricheck.client.ui.view_model.onboarding.OnboardingViewModel
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view_model.onboarding.OnboardingState

@Composable
fun OnboardingGoal(
    state : OnboardingState,
    onEvent : (OnboardingEvent) -> Unit,
) {
    var selectedGoal by remember { mutableStateOf<WeightGoal?>(null) }
    val error = state.errorState
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF000000))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .height(44.dp)
            ) {
                Image(
                    modifier = Modifier
                        .width(44.dp)
                        .height(44.dp),
                    painter = painterResource(id = R.drawable.nutri_check_apple_start),
                    contentDescription = "NutriCheck Logo",
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    text = stringResource(id = R.string.app_name),
                    style = TextStyle(
                        fontSize = 36.sp,
                        lineHeight = 44.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )
            }

            Column(
                modifier = Modifier.padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_question_goal),
                    style = TextStyle(
                        fontSize = 36.sp,
                        lineHeight = 44.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            enumValues<WeightGoal>().forEach { goal ->
                val textResId = when (goal) {
                    WeightGoal.GAIN_WEIGHT -> R.string.onboarding_label_goal_gain_weight
                    WeightGoal.LOSE_WEIGHT -> R.string.onboarding_label_goal_lose_weight
                    WeightGoal.MAINTAIN_WEIGHT -> R.string.onboarding_label_goal_maintain_weight
                }

                SelectOption(
                    text = stringResource(id = textResId),
                    onClick = { selectedGoal = goal },
                    selected = selectedGoal == goal
                )
            }
            error?.let { resId ->
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = stringResource(id = resId),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .height(40.dp)
                .width(286.dp),
            shape = RoundedCornerShape(size = 28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4580FF)
            ),
            onClick = {
                onEvent(OnboardingEvent.EnterWeightGoal(selectedGoal))
            })
        {
            Text(
                text = stringResource(id = R.string.onboarding_button_next),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}
