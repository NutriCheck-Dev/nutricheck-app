package com.frontend.nutricheck.client

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import com.frontend.nutricheck.client.model.data_sources.data.ThemeSetting
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NutriCheckApplication : Application()

object AppThemeState {
    var currentTheme = mutableStateOf(ThemeSetting.DARK)
}
