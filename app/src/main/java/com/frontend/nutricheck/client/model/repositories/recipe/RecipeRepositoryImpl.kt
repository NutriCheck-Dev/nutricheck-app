package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.dto.ErrorResponseDTO
import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.IngredientDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.search.RecipeSearchDao
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.search.RecipeSearchEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbIngredientMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.repositories.mapper.RecipeMapper
import com.frontend.nutricheck.client.model.repositories.mapper.ReportMapper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.jvm.java

class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val recipeSearchDao: RecipeSearchDao,
    private val ingredientDao: IngredientDao,
    private val foodDao: FoodDao,
    private val api: RemoteApi
) : RecipeRepository {
    private val timeToLive = TimeUnit.MINUTES.toMillis(30)

    override suspend fun searchRecipes(recipeName: String): Flow<Result<List<Recipe>>> = flow {
        val cached = recipeSearchDao.resultsFor(recipeName)
            .firstOrNull()
            ?.map { DbRecipeMapper.toRecipe(it) }
            ?: emptyList()
        if (cached.isNotEmpty()) {
            emit(Result.Success(cached))
        }

        val lastUpdate = recipeSearchDao.getLatestUpdatedFor(recipeName)
        if (isExpired(lastUpdate)) {
            try {
                val response = api.searchRecipes(recipeName)
                val body = response.body()
                val errorBody = response.errorBody()

                if (response.isSuccessful && body != null) {
                    val now = System.currentTimeMillis()
                    val recipes = body.map { RecipeMapper.toData(it) }

                    val ids = recipes.map { it.id }
                    val ownedIds: Set<String> =
                        if (ids.isEmpty()) {
                            emptySet()
                        } else {
                            recipeDao.getVisibilityById(ids)
                                .asSequence()
                                .filter { it.visibility == RecipeVisibility.OWNER }
                                .map { it.id }
                                .toSet()
                        }
                    val merged = recipes.map { remote ->
                        if (remote.id in ownedIds) remote.copy(visibility = RecipeVisibility.OWNER)
                        else remote
                    }

                    merged.forEach { cacheRemoteRecipe(it) }
                    recipeSearchDao.clearQuery(recipeName)
                    recipeSearchDao.upsertEntities(merged.map {
                        RecipeSearchEntity(recipeName, it.id, now)
                    })
                    emit(Result.Success(merged))
                } else if (errorBody != null) {
                    val errorResponse = Gson().fromJson(
                    errorBody.string(),
                    ErrorResponseDTO::class.java)
                    val message = errorResponse.body.title + ": " + errorResponse.body.detail
                    emit(Result.Error(errorResponse.body.status, message))
                } else {
                    emit(Result.Error(message = "Unknown error"))
                }
            } catch (io: okio.IOException) {
                emit(Result.Error(message = "Oops, an error has occurred. Please check your internet connection."))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun insertRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe, false)
        recipeDao.insert(recipeEntity)

        recipe.ingredients.forEach { ingredient ->
            val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
            checkForFoodProducts(ingredient)
            ingredientDao.insert(ingredientEntity)
        }
    }


    override suspend fun getMyRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        val recipesWithIngredients = recipeDao.getAllRecipesWithIngredients(RecipeVisibility.OWNER)
        val list = recipesWithIngredients.first()
        list.map { recipeWithIngredients ->
            DbRecipeMapper.toRecipe(recipeWithIngredients)
        }
    }

    override suspend fun observeMyRecipes(): Flow<List<Recipe>> =
        recipeDao.getAllRecipesWithIngredients(RecipeVisibility.OWNER)
            .map { list ->
                list.map(DbRecipeMapper::toRecipe)
            }
            .flowOn(Dispatchers.IO)

    override suspend fun getRecipeById(recipeId: String): Recipe = withContext(Dispatchers.IO) {
        val recipeWithIngredients = recipeDao.getRecipeWithIngredientsById(recipeId).first()
        DbRecipeMapper.toRecipe(recipeWithIngredients)
    }

    override suspend fun deleteRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe, true)
        recipeDao.update(recipeEntity)
    }

    override suspend fun updateRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe, false)
        recipeDao.update(recipeEntity)

        ingredientDao.deleteIngredientsOfRecipe(recipeEntity.id)
        recipe.ingredients.forEach { ingredient ->
            val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
            checkForFoodProducts(ingredient)
            ingredientDao.insert(ingredientEntity)
        }
    }

    override suspend fun uploadRecipe(recipe: Recipe): Result<Recipe> = withContext(Dispatchers.IO) {
        try {
            val response = api.uploadRecipe(RecipeMapper.toDto(recipe))
            response.body()?.let { dto ->
                return@withContext Result.Success(RecipeMapper.toData(dto))
            }
            val code = response.code()
            val rawError = response.errorBody()?.use { it.string() }

            val parsed = rawError
                ?.takeIf { it.isNotBlank() }
                ?.let { runCatching { Gson().fromJson(it, ErrorResponseDTO::class.java) }.getOrNull() }

            val serverMessage = parsed?.body?.let { body ->
                listOfNotNull(body.title, body.detail)
                    .filter { it.isNotBlank() }
                    .joinToString { ": " }
            }

            val fallback = if (code == 401) {
                "Upload failed: Recipe already exists."
            } else { "Upload failed" }

            Result.Error(parsed?.body?.status ?: code, message = serverMessage ?: fallback)
        } catch (io: IOException) {
            Result.Error(message = "Connection issue")
        }
    }


    override suspend fun reportRecipe(recipeReport: RecipeReport): Result<ReportDTO> = withContext(Dispatchers.IO) {
        try {
            val response = api.reportRecipe(ReportMapper.toDto(recipeReport))
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                Result.Success(body) //toData method
            } else if (errorBody != null) {
                val errorResponse = Gson().fromJson(
                    errorBody.string(),
                    ErrorResponseDTO::class.java)
                val message = errorResponse.body.title + ": "+ errorResponse.body.detail
                Result.Error(errorResponse.body.status, message)
            } else {
                Result.Error(message = "Unknown server error")
            }
        } catch (io: IOException) {
            Result.Error(message = "Connection issue")
        }
    }

    //Necessary?
    override suspend fun getIngredientById(
        recipeId: String,
        foodProductId: String
    ): Ingredient = withContext(Dispatchers.IO) {
        val ingredientWithFoodProduct = ingredientDao.getIngredientById(recipeId, foodProductId)
        DbIngredientMapper.toIngredient(ingredientWithFoodProduct!!)
    }

    override suspend fun updateIngredient(ingredient: Ingredient) = withContext(Dispatchers.IO) {
        val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
        ingredientDao.update(ingredientEntity)
    }

    override suspend fun getRecipesByName(recipeName: String): List<Recipe> = withContext(Dispatchers.IO) {
        val recipeWithIngredients = recipeDao.getRecipesByName(recipeName).first()
        recipeWithIngredients.map { DbRecipeMapper.toRecipe(it) }
    }

    override fun observeRecipeById(recipeId: String): Flow<Recipe> =
        recipeDao.getRecipeWithIngredientsById(recipeId)
            .map(DbRecipeMapper::toRecipe)
            .flowOn(Dispatchers.IO)

    private fun isExpired(lastUpdate: Long?): Boolean =
        lastUpdate == null || System.currentTimeMillis() - lastUpdate > timeToLive

    private suspend fun checkForFoodProducts(ingredient: Ingredient) = withContext(Dispatchers.IO) {
        if (!foodDao.exists(ingredient.foodProduct.id)) {
            foodDao.insert(DbFoodProductMapper.toFoodProductEntity(ingredient.foodProduct))
        }
    }

    private suspend fun cacheRemoteRecipe(recipe: Recipe) = withContext(Dispatchers.IO){
        val incoming = DbRecipeMapper.toRecipeEntity(recipe, false)

        val rowId = recipeDao.insertIgnore(incoming)
        if (rowId == -1L) {
            recipeDao.updateIfNotOwner(
                id = incoming.id,
                name = incoming.name,
                instructions = incoming.instructions,
                calories = incoming.calories,
                visibility = incoming.visibility
            )
        }

        val existing = recipeDao.getRecipeEntityById(incoming.id)
        if (existing?.visibility != RecipeVisibility.OWNER) {
            ingredientDao.deleteIngredientsOfRecipe(incoming.id)
            recipe.ingredients.forEach { ingredient ->
                checkForFoodProducts(ingredient)
                ingredientDao.insert(DbIngredientMapper.toIngredientEntity(ingredient))
            }
        }
    }
}