package com.frontend.nutricheck.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.BottomNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.RootNavGraph
import com.frontend.nutricheck.client.ui.view_model.navigation.Screen
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

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
fun MainScreen(
    hiltWrapperViewModel: HiltWrapperViewModel = hiltViewModel()
) {
    val mainNavController = rememberNavController()
    val backStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination?.route ?: Screen.HomePage.route
    val startDestination by produceState<String?>(initialValue = null, hiltWrapperViewModel) {
        val isOnboardingCompleted =
            hiltWrapperViewModel.appSettingRepository.isOnboardingCompleted.first()
        value = if (isOnboardingCompleted) {
            Screen.HomePage.route
        } else {
            Screen.Onboarding.route
        }
    }
    startDestination?.let {
        Scaffold(
            bottomBar = {
                if (currentDestination != Screen.Onboarding.route) {
                    BottomNavigationBar(
                        currentDestination = currentDestination,
                        onClickAdd = { mainNavController.navigate(Screen.Add.route) },
                        onClickHome = { mainNavController.navigate(Screen.HomePage.route) },
                        onClickDiary = { mainNavController.navigate(Screen.DiaryPage.route) },
                        onClickProfile = { mainNavController.navigate(Screen.ProfilePage.route) }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                RootNavGraph(mainNavController, startDestination!!)
            }
        }
    }


}
@HiltViewModel
class HiltWrapperViewModel @Inject constructor(
    val appSettingRepository: AppSettingRepository
) : ViewModel()



