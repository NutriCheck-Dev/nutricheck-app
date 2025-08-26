@file:OptIn(ExperimentalCoroutinesApi::class)

package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.SavedStateHandle
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.utils.CombinedSearchListStore
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.Context
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FoodProductOverviewViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var foodProductRepository: FoodProductRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var historyRepository: HistoryRepository
    private lateinit var combinedStore: CombinedSearchListStore
    private lateinit var context: Context


    private val foodProduct = FoodProduct(
        id = "fp1",
        name = "Pasta",
        calories = 200.0,
        carbohydrates = 30.0,
        protein = 10.0,
        fat = 5.0,
        servings = 1.0,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        foodProductRepository = mockk()
        recipeRepository = mockk()
        historyRepository = mockk()
        combinedStore = mockk()
        context = mockk(relaxed = true)
    }

    private fun makeViewModel(handle: SavedStateHandle): FoodProductOverviewViewModel =
        FoodProductOverviewViewModel(foodProductRepository, recipeRepository, historyRepository, combinedStore, handle)

    @Test
    fun `init FromSearch pulls item from combined store and computes params`() = runTest {
        every { combinedStore.state } returns MutableStateFlow(listOf(foodProduct))

        val viewModel = makeViewModel(SavedStateHandle(mapOf("foodProductId" to "fp1")))
        advanceUntilIdle()

        val state = viewModel.foodProductViewState.value
        assertEquals("Pasta", state.foodProduct.name)
        assertEquals(1.0, state.parameters.servings)
        assertEquals(ServingSize.ONEHOUNDREDGRAMS, state.parameters.servingSize)
        assertEquals(200.0, state.parameters.calories)
        assertEquals(30.0, state.parameters.carbohydrates)
        assertEquals(10.0, state.parameters.protein)
        assertEquals(5.0, state.parameters.fat)
    }

    @Test
    fun `init FromSearch falls back to repository when not in combined store`() = runTest {
        every { combinedStore.state } returns MutableStateFlow(emptyList())
        coEvery { foodProductRepository.getFoodProductById("fp1") } returns foodProduct

        val viewModel = makeViewModel(SavedStateHandle(mapOf("foodProductId" to "fp1")))
        advanceUntilIdle()

        val state = viewModel.foodProductViewState.value
        assertEquals("Pasta", state.foodProduct.name)
        assertEquals(200.0, state.parameters.calories)
    }

    @Test
    fun `init FromIngredient loads ingredient and fills state`() = runTest {
        val ingredient = Ingredient(
            recipeId = "r1",
            foodProduct = foodProduct,
            servings = 2.0,
            servingSize = ServingSize.ONEHOUNDREDGRAMS
        )
        coEvery { recipeRepository.getIngredientById("r1", "fp1") } returns ingredient
        every { combinedStore.state } returns MutableStateFlow(emptyList())

        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to "r1", "foodProductId" to "fp1")))
        advanceUntilIdle()

        val state = viewModel.foodProductViewState.value
        assertEquals("r1", state.recipeId)
        assertEquals(2.0, state.parameters.servings)
        assertEquals(400.0, state.parameters.calories)
    }

    @Test
    fun `init FromMeal loads mealFoodItem and fills state`() = runTest {
        val mealFoodItem= MealFoodItem(
            mealId = "m1",
            foodProduct = foodProduct,
            servings = 3.0,
            servingSize = ServingSize.ONEHOUNDREDGRAMS
        )
        coEvery { historyRepository.getMealFoodItemById("m1", "fp1") } returns mealFoodItem
        every { combinedStore.state } returns MutableStateFlow(emptyList())

        val viewModel = makeViewModel(SavedStateHandle(mapOf("mealId" to "m1", "foodProductId" to "fp1")))
        advanceUntilIdle()

        val state = viewModel.foodProductViewState.value
        assertEquals("m1", state.mealId)
        assertEquals(3.0, state.parameters.servings)
        assertEquals(600.0, state.parameters.calories)
    }

    @Test
    fun `ServingsChanged updates servings and recalculates nutrients`() = runTest {
        every { combinedStore.state } returns MutableStateFlow(listOf(foodProduct))
        val viewModel = makeViewModel(SavedStateHandle(mapOf("foodProductId" to "fp1")))
        advanceUntilIdle()

        viewModel.onEvent(FoodProductOverviewEvent.ServingsChanged(2.0))
        advanceUntilIdle()

        val state = viewModel.foodProductViewState.value
        val grams = state.parameters.servingSize.getAmount()
        val expected = 2.0 * grams * (foodProduct.calories / 100)
        assertEquals(2.0, state.parameters.servings)
        assertEquals(expected, state.parameters.calories)
    }

    @Test
    fun `ServingSizeChanged updates servingSize and recalculates nutrients`() = runTest {
        every { combinedStore.state } returns MutableStateFlow(listOf(foodProduct))
        val viewModel = makeViewModel(SavedStateHandle(mapOf("foodProductId" to "fp1")))
        advanceUntilIdle()

        viewModel.onEvent(FoodProductOverviewEvent.ServingSizeChanged(ServingSize.TENGRAMS))
        advanceUntilIdle()

        val state = viewModel.foodProductViewState.value
        val grams = state.parameters.servingSize.getAmount()
        val expected = state.parameters.servings * grams * (foodProduct.calories / 100)
        assertEquals(ServingSize.TENGRAMS, state.parameters.servingSize)
        assertEquals(expected, state.parameters.calories)
    }

    @Test
    fun `ServingSizeDropdownClick toggles flag`() = runTest {
        every { combinedStore.state } returns MutableStateFlow(listOf(foodProduct))
        val viewModel = makeViewModel(SavedStateHandle(mapOf("foodProductId" to "fp1")))
        advanceUntilIdle()

        val before = viewModel.foodProductViewState.value.parameters.servingSizeDropDownExpanded
        viewModel.onEvent(FoodProductOverviewEvent.ServingSizeDropDownClick)
        advanceUntilIdle()
        val after = viewModel.foodProductViewState.value.parameters.servingSizeDropDownExpanded
        assertTrue(after != before)
    }

    @Test
    fun `SaveAndAddClick in FromIngredient updates ingredient and emits event`() = runTest {
        val ingredient = Ingredient(
            recipeId = "r1",
            foodProduct = foodProduct,
            servings = 2.0,
            servingSize = ServingSize.ONEHOUNDREDGRAMS
        )
        coEvery { recipeRepository.getIngredientById("r1", "fp1") } returns ingredient
        coEvery { recipeRepository.updateIngredient(any()) } just Runs
        every { combinedStore.state } returns MutableStateFlow(emptyList())

        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to "r1", "foodProductId" to "fp1")))
        advanceUntilIdle()

        val awaited = async { viewModel.events.first() }
        viewModel.onEvent(FoodProductOverviewEvent.SaveAndAddClick)
        advanceUntilIdle()

        coVerify {
            recipeRepository.updateIngredient(withArg { updated ->
                assertEquals("r1", updated.recipeId)
                assertEquals(foodProduct, updated.foodProduct)
                val expectedQuantity = updated.servings * updated.servingSize.getAmount()
                assertEquals(expectedQuantity, updated.quantity)
            })
        }
        assertTrue(awaited.await() is FoodProductOverviewEvent.UpdateIngredient)
    }

    @Test
    fun `SaveAndAddClick in FromMeal updates mealFoodItem and emits event`() = runTest {
        val mealFoodItem= MealFoodItem(
            mealId = "m1",
            foodProduct = foodProduct,
            servings = 3.0,
            servingSize = ServingSize.ONEHOUNDREDGRAMS
        )
        coEvery { historyRepository.getMealFoodItemById("m1", "fp1") } returns mealFoodItem
        coEvery { historyRepository.updateMealFoodItem(any()) } just Runs
        every { combinedStore.state } returns MutableStateFlow(emptyList())

        val viewModel = makeViewModel(SavedStateHandle(mapOf("mealId" to "m1", "foodProductId" to "fp1")))
        advanceUntilIdle()

        val awaited = async { viewModel.events.first() }
        viewModel.onEvent(FoodProductOverviewEvent.SaveAndAddClick)
        advanceUntilIdle()

        coVerify {
            historyRepository.updateMealFoodItem(withArg { updated ->
                assertEquals("m1", updated.mealId)
                assertEquals(foodProduct, updated.foodProduct)
            })
        }
        assertTrue(awaited.await() is FoodProductOverviewEvent.SubmitMealItem)
    }

    @Test
    fun `SaveAndAddClick in FromSearch does nothing`() = runTest {
        every { combinedStore.state } returns MutableStateFlow(listOf(foodProduct))
        val viewModel = makeViewModel(SavedStateHandle(mapOf("foodProductId" to "fp1")))
        advanceUntilIdle()

        viewModel.onEvent(FoodProductOverviewEvent.SaveAndAddClick)
        advanceUntilIdle()

        coVerify(exactly = 0) { recipeRepository.updateIngredient(any()) }
        coVerify(exactly = 0) { historyRepository.updateMealFoodItem(any()) }
    }

    @Test
    fun `DeleteAiMeal in FromMeal deletes the meal`() = runTest {
        val meal = Meal(
            id = "m1",
            calories = 0.0, carbohydrates = 0.0, protein = 0.0, fat = 0.0,
            date = java.util.Date(), dayTime = DayTime.BREAKFAST,
            mealFoodItems = emptyList(), mealRecipeItems = emptyList()
        )
        coEvery { historyRepository.getMealById("m1") } returns meal
        coEvery { historyRepository.deleteMeal(meal) } just Runs
        coEvery { historyRepository.getMealFoodItemById(any(), any())
        } returns MealFoodItem("m1", foodProduct)
        every { combinedStore.state } returns MutableStateFlow(emptyList())

        val viewModel = makeViewModel(SavedStateHandle(mapOf("mealId" to "m1", "foodProductId" to "fp1")))
        advanceUntilIdle()

        viewModel.onEvent(FoodProductOverviewEvent.DeleteAiMeal)
        advanceUntilIdle()

        coVerify { historyRepository.deleteMeal(meal) }
    }

}