package com.frontend.nutricheck.client.ui.view.app_views.onboarding

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.ui.view_model.OnboardingEvent
import com.frontend.nutricheck.client.R

/**
 * OnboardingWelcome composable function displays the first screen of the onboarding process.
 *
 * @param onEvent Callback function to handle onboarding events.
 */
@Composable
fun OnboardingWelcome(
    onEvent : (OnboardingEvent) -> Unit,
) {
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
                    text = stringResource(id = R.string.onboarding_title),
                    style = TextStyle(
                        fontSize = 45.sp,
                        lineHeight = 52.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = stringResource(id = R.string.onboarding_description),
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0x99FFFFFF),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    )
                )
                Image(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .width(150.dp)
                        .height(150.dp),
                    painter = painterResource(id = R.drawable.onboarding_graph),
                    contentDescription = "Onboarding Graph",

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
                onEvent(OnboardingEvent.StartOnboarding)
            })
        {
            Text(
                text = stringResource(id = R.string.onboarding_button_start),
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
