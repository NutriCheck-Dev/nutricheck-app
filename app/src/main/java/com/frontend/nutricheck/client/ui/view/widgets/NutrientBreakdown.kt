package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em


@Composable
fun NutrientBreakdown(
    modifier: Modifier = Modifier,
    nutrients: Map<String, Int>
) {
    Surface(

        color = Color(0xff121212),
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
                text = "MAKROS",
                color = Color.White,
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
                MacroProgress(label = "Eiweiß", value = "76g", progress = 83f / 146f)
                MacroProgress(label = "Kohlenhydrate", value = "220g", progress = 108f / 146f)
                MacroProgress(label = "Fett", value = "58g", progress = 54f / 146f)
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
    Column(
        modifier = Modifier.requiredWidth(150.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                color = Color.White,
                lineHeight = 1.23.em,
            )
            Text(
                text = value,
                color = Color.White,
                textAlign = TextAlign.End,
                lineHeight = 1.23.em,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xffd9d9d9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(2.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xff4580ff))
            )
        }
    }
}

@Preview
@Composable
fun NutrientBreakdownPreview() {
    NutrientBreakdown(
        nutrients = mapOf(
            "Eiweiß" to 76,
            "Kohlenhydrate" to 220,
            "Fett" to 58
        ),
        modifier = Modifier

            .padding(16.dp)
    )
}