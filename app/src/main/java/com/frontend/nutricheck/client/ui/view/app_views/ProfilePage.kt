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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutricheck.frontend.R
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun ProfilePage(
    actions: NavigationActions,
    username : String,
    userAge : Int,
    userHeight : Int,
    userWeight : Double,
    darkmode : Boolean
) {
    val greetingText = stringResource(id = R.string.profile_name, username)
    val userHeightText = stringResource(id = R.string.height_cm, userHeight)
    val userWeightText = stringResource(id = R.string.weight_kg, userWeight)
    val userAgeText = stringResource(id = R.string.age_years, userAge)
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
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Row (
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.profile_menu_age),
                            fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text (userAgeText,
                            fontSize = 16.sp)
                    }
                    Row (
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.profile_menu_height),
                            fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(userHeightText,
                            fontSize = 16.sp)
                    }
                    Row (
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.profile_menu_weight),
                            fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(userWeightText,
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
                    MenuItem(
                        icon = Icons.Default.AccountCircle,
                        stringResource(id = R.string.profile_menu_item_personal_data),
                        onClick = { /* Handle click */ })
                    Divider(color = Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        thickness = 2.dp)
                    MenuItem(
                        icon = Icons.Default.BarChart,
                        stringResource(id = R.string.profile_menu_item_weight_history),
                        onClick = { /* Handle click */ })
                    Divider(color = Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        thickness = 2.dp
                    )
                    MenuItemWithSwitch(
                        icon = Icons.Filled.DarkMode,
                        text = stringResource(id = R.string.profile_menu_item_darkmode),
                        isChecked = darkmode,
                        onCheckedChange = { darkmode = it },
                    )
                    Divider(color = Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        thickness = 2.dp)
                    MenuItem(
                        icon = Icons.Filled.Language,
                        stringResource(id = R.string.profile_menu_item_language),
                        onClick = { /* Handle click */ })
                }
            }
        }
    }
}
@Composable
fun MenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(16.dp, top = 24.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text,
            color = Color.White,
            fontSize = 16.sp,)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White
        )
        Spacer (modifier = Modifier.width(24.dp))
    }
}

@Composable
fun MenuItemWithSwitch(
    icon: ImageVector,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onCheckedChange(!isChecked) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF4580FF)),
                checked = isChecked,
                onCheckedChange = onCheckedChange,
            )
        }
        Spacer (modifier = Modifier.width(8.dp))
    }
}


@Preview
@Composable
fun ProfilePagePreview() {
    val navController = NavHostController(context = androidx.compose.ui.platform.LocalContext.current)
    ProfilePage(
        actions = NavigationActions(navController),
        username = "Moritz",
        userAge = 25,
        userHeight = 180,
        userWeight = 75.0,
        darkmode = true
    )
    }

