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
/**
 * The main and only activity of the application.
 * It serves as the entry point and hosts the Jetpack Compose content.
 */
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
/**
 * The main screen composable, which holds the bottom navigation bar and the start destination of
 * the app's navigation graph.
 *
 * @param hiltWrapperViewModel A ViewModel to provide dependencies like repositories to this top-level composable.
 */
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
                        onClickAdd = { mainNavController.navigate(Screen.AddButton.route) },
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
/**
 * A helper ViewModel used to inject dependencies into top-level composables like [MainScreen].
 *
 * @property appSettingRepository The repository for accessing application settings.
 */
@HiltViewModel
class HiltWrapperViewModel @Inject constructor(
    val appSettingRepository: AppSettingRepository
) : ViewModel()



