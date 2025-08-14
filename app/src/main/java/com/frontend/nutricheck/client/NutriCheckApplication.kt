package com.frontend.nutricheck.client

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.frontend.nutricheck.client.model.data_sources.data.flags.ThemeSetting
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.frontend.nutricheck.client.model.repositories.CachePruneWorker
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
/**
 * The main Application class for the NutriCheck app.
 * It initializes Hilt for dependency injection and sets up an observer for the
 * application's theme setting to allow for dynamic theme changes.
 */
@HiltAndroidApp
class NutriCheckApplication :
    Application(), Configuration.Provider {

        @Inject lateinit var workerFactory: HiltWorkerFactory
        @Inject lateinit var appSettingRepository: AppSettingRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Default).launch {
            appSettingRepository.theme.collect {isDarkMode ->
                AppThemeState.currentTheme.value =
                    if (isDarkMode) ThemeSetting.DARK else ThemeSetting.LIGHT
            }
        }
        scheduleCachePrune()
    }

    private fun scheduleCachePrune() {
        val request = PeriodicWorkRequestBuilder<CachePruneWorker>(
            1, TimeUnit.HOURS,
            15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "cache_prune",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
/**
 * A singleton object that holds the global state for the application's current theme.
 * This allows composables throughout the app to react to theme changes.
 */
object AppThemeState {
    var currentTheme = mutableStateOf(ThemeSetting.DARK)
}
/**
 * A Hilt module for providing the DataStore instance as a dependency.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    /**
     * Provides a singleton instance of [DataStore<Preferences>] for the application.
     * The DataStore is used to persist application settings.
     *
     * @param context The application context provided by Hilt.
     * @return A singleton [DataStore<Preferences>] instance.
     */
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
