package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(
    navController: NavController,
    onAddClicked: () -> Unit
) {
    NavigationBar {
        val currentRoute = currentRoute(navController)
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = { /* Icon for Home */ },
            label = { /* Label for Home */ }
        )

        NavigationBarItem(
            selected = currentRoute == "diary",
            onClick = { navController.navigate("diary") },
            icon = { /* Icon for Diary */ },
            label = { /* Label for Diary */ }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
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

@Composable
fun currentRoute(navController: NavController): String? {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
}