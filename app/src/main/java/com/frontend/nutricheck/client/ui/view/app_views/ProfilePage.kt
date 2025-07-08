package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.ProfileOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions
import com.nutricheck.frontend.R


@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    profilePageViewModel: ProfileOverviewViewModel = hiltViewModel(),
    onPersonalDataClick: () -> Unit = {},
    onWeightHistoryClick: () -> Unit = {},
    onThemeToggleClick: (Boolean) -> Unit = {},
    onLanguageClick: (String) -> Unit = {},
) {
}

@Preview
@Composable
fun ProfilePagePreview(username: String = "Moritz", darkmode : Boolean = true) {
    val greetingText = stringResource(id = R.string.profile_name, username)
    var darkmode by remember { mutableStateOf(darkmode) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color(0xFF000000))
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                Modifier
                    .shadow(elevation = 6.dp,
                        spotColor = Color(0x1FBBBBBB),
                        ambientColor = Color(0x1FBBBBBB))
                    .height(104.dp)
                    .fillMaxWidth()
                    .background(color = Color(0xFF121212), shape = RoundedCornerShape(size = 16.dp))
                    .padding(start = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = greetingText,
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                        letterSpacing = 0.1.sp,
                    ),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF121212),
                    contentColor = Color.White)
            ) {
                Column() {
                    Row (
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.profile_menu_age),
                            fontSize = 16.sp)
                    }
                    Row (
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.profile_menu_height),
                            fontSize = 16.sp)
                    }
                    Row (
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.profile_menu_weight),
                            fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF121212),
                    contentColor = Color.White
                ),
                ) {
                    Column {
                        MenuItem("Persönliche Daten & Ziel",
                            onClick = { /* Handle click */ })
                        Divider(color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp))
                        MenuItem(
                            "Gewichtsverlauf",
                            onClick = { /* Handle click */ })
                        Divider(color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp))
                        MenuItemWithSwitch(
                            "Optik",
                            isChecked = darkmode,
                            onCheckedChange = { darkmode = it },
                        )
                        Divider(color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp))
                        MenuItem(
                            "Sprache",
                            onClick = { /* Handle click */ })
                    }
                }
            }
        }
    }
@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun MenuItemWithSwitch(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    ) {
    Row(
        modifier = Modifier
            .clickable { onCheckedChange(!isChecked) }
            .fillMaxWidth()
        .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                )
            Spacer(modifier = Modifier.width(8.dp))
            }
    }
}