package com.frontend.nutricheck.client

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.mutableStateOf
import com.frontend.nutricheck.client.model.data_sources.data.Language
import com.frontend.nutricheck.client.model.data_sources.data.ThemeSetting
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class NutriCheckApplication : Application() {
    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository
//    override fun attachBaseContext(base: Context?) {
//        val languageCode = runBlocking {
//            try {
//                appSettingsRepository.language.first().code
//            } catch (_: NoSuchElementException) {
//                Language.GERMAN.code
//            }
//        }
//        val locale = Locale(languageCode)
//        val config = Configuration()
//        config.setLocale(locale)
//        val context = base?.createConfigurationContext(config)
//        super .attachBaseContext(context)
//
//    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Default).launch {
            appSettingsRepository.theme.collect {isDarkMode ->
                AppThemeState.currentTheme.value =
                    if (isDarkMode) ThemeSetting.DARK else ThemeSetting.LIGHT
            }
        }
    }
}

object AppThemeState {
    var currentTheme = mutableStateOf(ThemeSetting.DARK)
}
