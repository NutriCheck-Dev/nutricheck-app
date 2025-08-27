package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.ui.view_model.navigation.Screen

/**
 * Composable function that represents the bottom navigation bar of the application.
 * It contains navigation items for Home, Diary, Profile, and Add.
 */
@Composable
fun BottomNavigationBar(
    currentDestination: String,
    onClickAdd: () -> Unit,
    onClickHome: () -> Unit,
    onClickDiary: () -> Unit,
    onClickProfile: () -> Unit
   ) {
    NavigationBar {

        NavigationBarItem(
            selected = currentDestination == Screen.HomePage.route,
            onClick = { onClickHome() },
            icon = { Icon(Icons.Default.Home,
                contentDescription = stringResource(id = R.string.navigation_bar_label_home)) },
            label = { Text(stringResource(id = R.string.navigation_bar_label_home)) }
        )

        NavigationBarItem(
            modifier = Modifier.semantics { contentDescription = SemanticsTags.BOTTOM_NAV_DIARY_PAGE },
            selected = currentDestination == Screen.DiaryPage.createRoute(null),
            onClick = { onClickDiary() },
            icon = { Icon(Icons.Default.DateRange,
                   contentDescription = stringResource(id = R.string.navigation_bar_label_diary)) },
            label = { Text(stringResource (id = R.string.navigation_bar_label_diary)) }
        )

        NavigationBarItem(
            selected = currentDestination == Screen.ProfilePage.route,
            onClick = { onClickProfile() },
            icon = { Icon((Icons.Default.AccountCircle),
                contentDescription = stringResource(id = R.string.navigation_bar_label_profile)) },
            label = { Text(stringResource(id = R.string.navigation_bar_label_profile)) }
        )

        NavigationBarItem(
            modifier = Modifier.semantics { contentDescription = SemanticsTags.BOTTOM_NAV_ADD },
            selected = currentDestination == "add",
            onClick = { onClickAdd() },
            icon = { Icon(Icons.Default.AddCircle,
                contentDescription = stringResource(id = R.string.navigation_bar_label_add)) },
            label = { Text(stringResource(id = R.string.navigation_bar_label_add)) }
        )
    }
}
