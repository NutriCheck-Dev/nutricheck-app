package com.frontend.nutricheck.client.model.repositories

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
/**
 * Worker that prunes expired and unreferenced food products and recipes from the local cache.
 */
@HiltWorker
class CachePruneWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val foodDao: FoodDao,
    private val recipeDao: RecipeDao
): CoroutineWorker(context, workerParams) {
    private val timeToLive = TimeUnit.MINUTES.toMillis(15)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - timeToLive
        val foods = foodDao.deleteExpiredUnreferencedFoodProducts(cutoff)
        val recipes = recipeDao.deleteExpiredUnreferencedRecipes(cutoff)
        Log.d("CachePrune", "Pruned foods=$foods, recipes=$recipes, cutoff=$cutoff")
        Result.success()
    }


}