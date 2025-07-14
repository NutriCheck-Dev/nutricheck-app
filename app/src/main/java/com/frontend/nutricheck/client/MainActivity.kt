package com.frontend.nutricheck.client

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.BottomNavigationBar
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationGraph
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.frontend.nutricheck.client.ui.view_model.navigation.Screen
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainScreen()

            }
        }
    }
}



@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination?.route ?: Screen.HomePage.route

    Scaffold(
        bottomBar = {
            if (currentDestination != null) {
                BottomNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    onAddClicked = { TODO("implement logic") }
                )
            }
        }
    )
    { innerPadding ->
        Box( modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController)
        }
    }
}



