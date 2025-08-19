package com.nutricheck.frontend

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.dto.MealItemDTO
import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealFoodItemWithProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealRecipeItemWithRecipe
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Date

object TestDataFactory {

    fun createDefaultFoodProductDTO() = FoodProductDTO(
        id = "testFoodProductId",
        name = "testName",
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0
    )

    fun createDefaultFoodProduct() = FoodProduct(
        id = "testFoodProductId",
        name = "testName",
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
        servings =  1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    fun createDefaultFoodProductEntity() = FoodProductEntity(
        id = "testFoodProductId",
        name = "testName",
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
    )

    fun createDefaultErrorMessage(): String {
        return "{\n" +
                "  \"statusCode\":\"BAD_REQUEST\",\n" +
                "  \"headers\":{},\n" +
                "  \"typeMessageCode\":\"problemDetail.type.org.springframework.web.bind.MethodArgumentNotValidException\",\n" +
                "  \"titleMessageCode\":\"problemDetail.title.org.springframework.web.bind.MethodArgumentNotValidException\",\n" +
                "  \"detailMessageCode\":\"problemDetail.org.springframework.web.bind.MethodArgumentNotValidException\",\n" +
                "  \"detailMessageArguments\":null,\n" +
                "  \"body\":\n" +
                "  {\n" +
                "    \"type\":\"about:blank\",\n" +
                "    \"title\":\"Bad Request\",\n" +
                "    \"status\":400,\n" +
                "    \"detail\":\"Invalid request content.\"\n" +
                "  }\n" +
                "}"
    }

    fun createDefaultRecipeDTO() = RecipeDTO(
        id = "testRecipeId",
        name = "testRecipe",
        instructions = "test instructions",
        servings = 1,
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
        ingredients = listOf(createDefaultIngredientDTO())
    )

    fun createDefaultIngredientDTO() = IngredientDTO(
        recipeId = "testRecipeId",
        foodProductId = "testFoodProductId",
        foodProduct = createDefaultFoodProductDTO(),
        quantity = 100.0,
    )

    fun createDefaultRecipe() = Recipe(
        id = "testRecipeId",
        name = "testRecipe",
        instructions = "test instructions",
        servings = 1,
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
        ingredients = listOf(createDefaultIngredient())
    )

    fun createDefaultIngredient() = Ingredient(
        recipeId = "testRecipeId",
        foodProduct = createDefaultFoodProduct(),
        quantity = 100.0,
    )

    fun createDefaultRecipeEntity() = RecipeEntity(
        id = "testRecipeId",
        name = "testRecipe",
        instructions = "test instructions",
        servings = 1.0,
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
        visibility = RecipeVisibility.OWNER,
        deleted = false
    )

    fun createDefaultRecipeEntityDeleted() = RecipeEntity(
        id = "testRecipeId",
        name = "testRecipe",
        instructions = "test instructions",
        servings = 1.0,
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
        visibility = RecipeVisibility.OWNER,
        deleted = true
    )



    fun createDefaultRecipeWithIngredients() = RecipeWithIngredients(
        recipe = createDefaultRecipeEntity(),
        ingredients = listOf(createDefaultIngredientWithFoodProduct())
    )

    fun createDefaultIngredientEntity() = IngredientEntity(
        recipeId = "testRecipeId",
        foodProductId = "testFoodProductId",
        quantity = 100.0,
        servings = 1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS,
    )

    fun createDefaultIngredientWithFoodProduct() = IngredientWithFoodProduct(
        ingredient = createDefaultIngredientEntity(),
        foodProduct = createDefaultFoodProductEntity()
    )
    
    fun createDefaultReport() = RecipeReport(
        description = "test report",
        recipeId = "testRecipeId",
        recipeName = "testRecipe",
        recipeInstructions = "test instructions",
    )
    
    fun createDefaultReportDTO() = ReportDTO(
        description = "test report",
        recipeId = "testRecipeId"
    )

    fun createDefaultMealItemDTO() = MealItemDTO(
        foodProduct = createDefaultFoodProductDTO(),
        foodProductId = "testFoodProductId",
    )

    fun createDefaultMealItem() = MealFoodItem(
        mealId = "testMealId",
        foodProduct = createDefaultFoodProduct(),
        quantity = 1.0,
        servings = 1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    fun createDefaultMealRecipeItem() = MealRecipeItem(
        mealId = "testMealId",
        recipe = createDefaultRecipe(),
        quantity = 1.0,
        servings = 1
    )

    fun createDefaultMealDTO() = MealDTO(
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
        items = setOf(createDefaultMealItemDTO())
    )


    fun createDefaultMeal() = Meal(
        id = "testMealId",
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0,
        date = Date(),
        dayTime = DayTime.dateToDayTime(Date()),
        mealFoodItems = listOf(createDefaultMealItem()),
        mealRecipeItems = listOf(createDefaultMealRecipeItem())
    )

    fun createDefaultMealEntity() = MealEntity(
        id = "testMealId",
        historyDayDate = Date(),
        dayTime = DayTime.dateToDayTime(Date()),
        calories = 0.0,
        carbohydrates = 1.0,
        protein = 2.0,
        fat = 3.0
    )

    fun createDefaultMealFoodItemEntity() = MealFoodItemEntity(
        mealId = "testMealId",
        foodProductId = "testFoodProductId",
        quantity = 1.0,
        servings = 1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    fun createDefaultMealRecipeItemEntity() = MealRecipeItemEntity(
        mealId = "testMealId",
        recipeId = "testRecipeId",
        quantity = 1.0
    )

    fun createDefaultFoodItemsWithProduct() = MealFoodItemWithProduct(
        mealFoodItem = createDefaultMealFoodItemEntity(),
        foodProduct = createDefaultFoodProductEntity()
    )
    fun createDefaultMealRecipeItemWithRecipe() = MealRecipeItemWithRecipe(
        mealRecipeItem = createDefaultMealRecipeItemEntity(),
        recipeWithIngredients = createDefaultRecipeWithIngredients()
    )

    fun createDefaultMealWithAll() = MealWithAll(
        meal = createDefaultMealEntity(),
        mealFoodItems = listOf(createDefaultFoodItemsWithProduct()),
        mealRecipeItems = listOf(createDefaultMealRecipeItemWithRecipe())
    )

    fun createDefaultRecipeJson(): String {
        return "{\n" +
                "  \"id\":\"testRecipeId\",\n" +
                "  \"name\":\"testRecipe\",\n" +
                "  \"instructions\":\"test instructions\",\n" +
                "  \"servings\":1,\n" +
                "  \"calories\":0.0,\n" +
                "  \"carbohydrates\":1.0,\n" +
                "  \"protein\":2.0,\n" +
                "  \"fat\":3.0,\n" +
                "  \"ingredients\":[\n" +
                "    {\n" +
                "      \"recipeId\":\"testRecipeId\",\n" +
                "      \"foodProductId\":\"testFoodProductId\",\n" +
                "      \"foodProduct\":{\n" +
                "        \"id\":\"testFoodProductId\",\n" +
                "        \"name\":\"testName\",\n" +
                "        \"calories\":0.0,\n" +
                "        \"carbohydrates\":1.0,\n" +
                "        \"protein\":2.0,\n" +
                "        \"fat\":3.0\n" +
                "      },\n" +
                "      \"quantity\":100.0\n" +
                "    }\n" +
                "  ]\n" +
                "}"
    }

    fun createDefaultReportJson(): String {
        return "{\n" +
                "  \"description\":\"test report\",\n" +
                "  \"recipeId\":\"testRecipeId\",\n" +
                "}"
    }

    fun createDefaultMealJson(): String {
        return "{\n" +
                "  \"calories\":0.0,\n" +
                "  \"carbohydrates\":1.0,\n" +
                "  \"protein\":2.0,\n" +
                "  \"fat\":3.0,\n" +
                "  \"items\":[\n" +
                "    {\n" +
                "      \"foodProduct\":{\n" +
                "        \"id\":\"testFoodProductId\",\n" +
                "        \"name\":\"testName\",\n" +
                "        \"calories\":0.0,\n" +
                "        \"carbohydrates\":1.0,\n" +
                "        \"protein\":2.0,\n" +
                "        \"fat\":3.0\n" +
                "      },\n" +
                "      \"foodProductId\":\"testFoodProductId\"\n" +
                "    }\n" +
                "  ]\n" +
                "}"
    }

    fun createDefaultFoodProductListJson(): String {
        return "[\n" +
                "  {\n" +
                "    \"id\":\"testFoodProductId\",\n" +
                "    \"name\":\"testName\",\n" +
                "    \"calories\":0.0,\n" +
                "    \"carbohydrates\":1.0,\n" +
                "    \"protein\":2.0,\n" +
                "    \"fat\":3.0\n" +
                "  },\n" +
                "  {\n" +
                    "\"id\":\"testFoodProductId\",\n" +
                    "\"name\":\"testName\",\n" +
                    "\"calories\":0.0,\n" +
                    "\"carbohydrates\":1.0,\n" +
                    "\"protein\":2.0,\n" +
                    "\"fat\":3.0\n" +
                "  }\n" +
                "]"
    }

    fun createDefaultMultipartBody(): MultipartBody.Part {
        val file = "fake-image-content".toByteArray()

        val requestBody = file.toRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            name = "file",
            filename = "test_image.jpg",
            body = requestBody
        )
    }

}