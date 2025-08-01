package com.frontend.nutricheck.client

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.frontend.nutricheck.client.model.data_sources.data.flags.ThemeSetting
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class NutriCheckApplication : Application() {
    @Inject
    lateinit var appSettingRepository: AppSettingRepository

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Default).launch {
            appSettingRepository.theme.collect {isDarkMode ->
                AppThemeState.currentTheme.value =
                    if (isDarkMode) ThemeSetting.DARK else ThemeSetting.LIGHT
            }
        }
    }
}

object AppThemeState {
    var currentTheme = mutableStateOf(ThemeSetting.DARK)
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { File(context.filesDir, "settings.preferences_pb") }
        )
    }
}
