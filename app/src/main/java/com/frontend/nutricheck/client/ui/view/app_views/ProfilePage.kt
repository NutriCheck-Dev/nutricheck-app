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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.R
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.tooling.preview.Preview
import com.frontend.nutricheck.client.AppThemeState.currentTheme
import com.frontend.nutricheck.client.model.data_sources.data.flags.ThemeSetting
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.ui.view_model.ProfileEvent

@Composable
fun ProfilePage(
    state : UserData,
    onEvent : (ProfileEvent) -> Unit,
   ) {

    val greetingText = stringResource(id = R.string.profile_name, state.username)
    val userHeightText = stringResource(id = R.string.height_cm, state.height.toString())
    val userWeightText = stringResource(id = R.string.weight_kg, state.weight.toString())
    val userAgeText = stringResource(id = R.string.age_years, state.age)
    val scrollState = rememberScrollState()
    val darkmode = currentTheme.value == ThemeSetting.DARK

    val sixteenDp = 16.dp
    val eightDp = 8.dp
    val thirtyTwoDp = 32.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(eightDp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(thirtyTwoDp))
            Row(
                Modifier
                    .shadow(elevation = 6.dp)
                    .height(104.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF121212), shape = RoundedCornerShape(sixteenDp))
                    .padding(start = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = greetingText,
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight(700),
                        letterSpacing = 0.1.sp,
                        color = Color.White
                    ),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(thirtyTwoDp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(sixteenDp),
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
                        contentDescription =
                            stringResource(id = R.string.profile_menu_item_personal_data),
                        text = stringResource(id = R.string.profile_menu_item_personal_data),
                        onClick = {
                            onEvent(ProfileEvent.OnPersonalDataClick)
                        })
                    HorizontalDivider(color = Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        thickness = 2.dp)
                    MenuItem(
                        icon = Icons.Default.BarChart,
                        contentDescription =
                            stringResource(id = R.string.profile_menu_item_weight_history),
                        text = stringResource(id = R.string.profile_menu_item_weight_history),
                        onClick = {
                            onEvent(ProfileEvent.DisplayWeightHistory)
                        })
                    HorizontalDivider(color = Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        thickness = 2.dp
                    )
                    MenuItemWithSwitch(
                        icon = Icons.Filled.DarkMode,
                        contentDescription =
                            stringResource(id = R.string.profile_menu_item_darkmode),
                        text = stringResource(id = R.string.profile_menu_item_darkmode),
                        checked = darkmode,
                        onCheckedChange = { onEvent(ProfileEvent.ChangeTheme(
                            if (it) ThemeSetting.DARK else ThemeSetting.LIGHT)) },
                    )
                }
            }
        }
    }
}
@Composable
fun MenuItem(icon: ImageVector, contentDescription : String, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(16.dp, top = 24.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon,
            contentDescription,
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
    contentDescription: String,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onCheckedChange(!checked) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(shape = RoundedCornerShape(8.dp), color = Color(0xFF121212))
            .padding(top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF4580FF)),
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
        Spacer (modifier = Modifier.width(8.dp))
    }
}

@Preview
@Composable
fun ProfilePagePreview() {
    ProfilePage(
        state = UserData(
            username = "John Doe",
            height = 180.0,
            weight = 75.0,
            age = 30
        ),
        onEvent = {}
    )
}


