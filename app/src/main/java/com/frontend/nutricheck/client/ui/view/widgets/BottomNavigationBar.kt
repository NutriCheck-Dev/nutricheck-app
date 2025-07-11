package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions


@Composable
fun BottomNavigationBar(
    actions: NavigationActions,
    selected: String,
    onAddClicked: () -> Unit = {}
) {
    NavigationBar {
        var selectedTab by rememberSaveable { mutableStateOf(selected) }

        NavigationBarItem(
            selected = selectedTab == "home",
            onClick = {
                actions.toHome()
                selectedTab = "home"
                      },
            icon = { /* Icon for Home */ },
            label = { /* Label for Home */ }
        )

        NavigationBarItem(
            selected = selectedTab == "diary",
            onClick = {
                actions.toDiary()
                selectedTab = "diary"
                      },
            icon = { /* Icon for Diary */ },
            label = { /* Label for Diary */ }
        )

        NavigationBarItem(
            selected = selectedTab == "profile",
            onClick = {
                actions.toProfile()
                selectedTab = "profile"
                      },
            icon = { /* Icon for Profile */ },
            label = { /* Label for Profile */ }
        )

        NavigationBarItem(
            selected = false,
            onClick = { onAddClicked() },
            icon = { Icon(Icons.Default.Add, contentDescription = "Hinzufügen") },
            label = { Text("Hinzufügen") }
        )
    }
}
