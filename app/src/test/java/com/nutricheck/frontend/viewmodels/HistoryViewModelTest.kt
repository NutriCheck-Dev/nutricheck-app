package com.nutricheck.frontend.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.HistoryEvent
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

/**
 * Unit tests for [com.frontend.nutricheck.client.ui.view_model.HistoryViewModel]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyRepository: HistoryRepository
    private lateinit var userDataRepository: UserDataRepository

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // Test data - Food Product
    private val testFoodProduct = FoodProduct(
        id = "food123",
        name = "Test Food",
        calories = 100.0,
        carbohydrates = 20.0,
        protein = 5.0,
        fat = 2.0,
        servings = 1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    // Test data - Recipe
    private val testRecipe = Recipe(
        id = "recipe123",
        name = "Test Recipe",
        calories = 300.0,
        carbohydrates = 40.0,
        protein = 15.0,
        fat = 10.0,
        servings = 2
    )

    // Test data - Meal Items
    private val testMealFoodItem = MealFoodItem(
        mealId = "meal123",
        foodProduct = testFoodProduct,
        quantity = 100.0,
        servings = 1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    private val testMealRecipeItem = MealRecipeItem(
        mealId = "meal123",
        recipe = testRecipe,
        quantity = 150.0,
        servings = 1
    )

    // Test data - Core objects
    private val testDate = Date(1704067200000L) // 2024-1-1
    private val testMealId = "meal123"
    private val testFoodId = "food456"
    private val testRecipeId = "recipe789"
    private val testDetailsId = "details123"

    private val testMeal = Meal(
        id = testMealId,
        calories = 400.0,
        carbohydrates = 60.0,
        protein = 20.0,
        fat = 12.0,
        date = testDate,
        dayTime = DayTime.BREAKFAST,
        mealFoodItems = listOf(testMealFoodItem),
        mealRecipeItems = listOf(testMealRecipeItem)
    )

    @Before
    fun setup() {
        historyRepository = mockk(relaxed = true)
        userDataRepository = mockk(relaxed = true)

        // Setup default mock responses
        coEvery { historyRepository.observeMealsForDay(any()) } returns flowOf(listOf(testMeal))
        coEvery { historyRepository.observeCaloriesOfDay(any()) } returns flowOf(1500)
        coEvery { userDataRepository.getDailyCalorieGoal() } returns 2000

        viewModel = HistoryViewModel(historyRepository, userDataRepository)
    }

    /**
     * Test that the ViewModel initializes with correct default state
     */
    @Test
    fun `initial state should have correct default values`() = testScope.runTest {
        val initialState = viewModel.historyState.value

        assertNotNull(initialState.selectedDate)
        assertTrue(initialState.nutritionOfDay.isEmpty())
        assertTrue(initialState.mealsGrouped.isEmpty())
        assertEquals("", initialState.mealId)
        assertEquals("", initialState.foodId)
        assertEquals("", initialState.recipeId)
        assertEquals(DayTime.BREAKFAST, initialState.dayTime)
        assertEquals(0, initialState.totalCalories)
        assertEquals(0, initialState.goalCalories)
        assertFalse(initialState.switched)
    }

    /**
     * Test that ViewModel initializes and loads data for current date
     */
    @Test
    fun `initialization should trigger data loading for current date`() = testScope.runTest {
        advanceUntilIdle()

        // Verify that repository methods are called during initialization
        coVerify(atLeast = 1) { historyRepository.observeMealsForDay(any()) }
        coVerify(atLeast = 1) { historyRepository.observeCaloriesOfDay(any()) }
        coVerify(atLeast = 1) { userDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Test that selectDate updates state and triggers repository observations
     */
    @Test
    fun `selectDate should update selected date and trigger observations`() = testScope.runTest {
        // When
        viewModel.selectDate(testDate)
        advanceUntilIdle()

        // Then - verify state update
        val state = viewModel.historyState.value
        assertEquals(testDate, state.selectedDate)
        assertEquals(1500, state.totalCalories)
        assertEquals(2000, state.goalCalories)
        assertEquals(1, state.mealsGrouped.size)
        assertTrue(state.mealsGrouped.containsKey(DayTime.BREAKFAST))
        assertEquals(1, state.mealsGrouped[DayTime.BREAKFAST]?.size)

        // Verify repository interactions
        coVerify { historyRepository.observeMealsForDay(testDate) }
        coVerify { historyRepository.observeCaloriesOfDay(testDate) }
        coVerify { userDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Test AddEntryClick event handling
     */
    @Test
    fun `onEvent with AddEntryClick should update state and emit event`() = testScope.runTest {
        // When
        viewModel.onEvent(HistoryEvent.AddEntryClick(testDate, DayTime.LUNCH))
        advanceUntilIdle()

        // Then - verify state update
        val state = viewModel.historyState.value
        assertEquals(testDate, state.selectedDate)
        assertEquals(DayTime.LUNCH, state.dayTime)

        // Then - verify event emission by collecting events flow
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        val event = events[0] as HistoryEvent.AddEntryClick
        assertEquals(testDate, event.day)
        assertEquals(DayTime.LUNCH, event.dayTime)
    }

    /**
     * Test FoodClicked event handling
     */
    @Test
    fun `onEvent with FoodClicked should emit correct event`() = testScope.runTest {
        // When
        viewModel.onEvent(HistoryEvent.FoodClicked(testMealId, testFoodId))
        advanceUntilIdle()

        // Then - verify event emission
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        val event = events[0] as HistoryEvent.FoodClicked
        assertEquals(testMealId, event.mealId)
        assertEquals(testFoodId, event.foodId)
    }

    /**
     * Test RecipeClicked event handling
     */
    @Test
    fun `onEvent with RecipeClicked should emit correct event`() = testScope.runTest {
        // When
        viewModel.onEvent(HistoryEvent.RecipeClicked(testMealId, testRecipeId))
        advanceUntilIdle()

        // Then - verify event emission
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        val event = events[0] as HistoryEvent.RecipeClicked
        assertEquals(testMealId, event.mealId)
        assertEquals(testRecipeId, event.recipeId)
    }

    /**
     * Test DetailsClick event handling
     */
    @Test
    fun `onEvent with DetailsClick should emit correct event`() = testScope.runTest {
        // When
        viewModel.onEvent(HistoryEvent.DetailsClick(testDetailsId))
        advanceUntilIdle()

        // Then - verify event emission
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        val event = events[0] as HistoryEvent.DetailsClick
        assertEquals(testDetailsId, event.detailsId)
    }

    /**
     * Test RemoveMealItem event handling with MealFoodItem
     */
    @Test
    fun `onEvent with RemoveMealItem for MealFoodItem should call repository`() = testScope.runTest {
        // When
        viewModel.onEvent(HistoryEvent.RemoveMealItem(testMealFoodItem))
        advanceUntilIdle()

        // Then
        coVerify { historyRepository.removeMealItem(testMealFoodItem) }
    }

    /**
     * Test RemoveMealItem event handling with MealRecipeItem
     */
    @Test
    fun `onEvent with RemoveMealItem for MealRecipeItem should call repository`() = testScope.runTest {
        // When
        viewModel.onEvent(HistoryEvent.RemoveMealItem(testMealRecipeItem))
        advanceUntilIdle()

        // Then
        coVerify { historyRepository.removeMealItem(testMealRecipeItem) }
    }

    /**
     * Test SelectDate event handling
     */
    @Test
    fun `onEvent with SelectDate should update selected date`() = testScope.runTest {
        // When
        viewModel.onEvent(HistoryEvent.SelectDate(testDate))
        advanceUntilIdle()

        // Then
        val state = viewModel.historyState.value
        assertEquals(testDate, state.selectedDate)

        // Verify repository calls
        coVerify { historyRepository.observeMealsForDay(testDate) }
        coVerify { historyRepository.observeCaloriesOfDay(testDate) }
    }

    /**
     * Test that meals are properly grouped by DayTime
     */
    @Test
    fun `observeMeals should group meals by dayTime correctly`() = testScope.runTest {
        // Given
        val breakfastMeal = testMeal.copy(id = "breakfast", dayTime = DayTime.BREAKFAST)
        val lunchMeal = testMeal.copy(id = "lunch", dayTime = DayTime.LUNCH)
        val dinnerMeal = testMeal.copy(id = "dinner", dayTime = DayTime.DINNER)
        val snackMeal = testMeal.copy(id = "snack", dayTime = DayTime.SNACK)
        val meals = listOf(breakfastMeal, lunchMeal, dinnerMeal, snackMeal)

        coEvery { historyRepository.observeMealsForDay(any()) } returns flowOf(meals)

        // When
        viewModel.selectDate(testDate)
        advanceUntilIdle()

        // Then - collect state updates
        val states = viewModel.historyState.take(1).toList()
        val state = states[0]
        assertEquals(4, state.mealsGrouped.size)
        assertTrue(state.mealsGrouped.containsKey(DayTime.BREAKFAST))
        assertTrue(state.mealsGrouped.containsKey(DayTime.LUNCH))
        assertTrue(state.mealsGrouped.containsKey(DayTime.DINNER))
        assertTrue(state.mealsGrouped.containsKey(DayTime.SNACK))
        assertEquals(1, state.mealsGrouped[DayTime.BREAKFAST]?.size)
        assertEquals(1, state.mealsGrouped[DayTime.LUNCH]?.size)
        assertEquals(1, state.mealsGrouped[DayTime.DINNER]?.size)
        assertEquals(1, state.mealsGrouped[DayTime.SNACK]?.size)
    }

    /**
     * Test multiple meals in same day time are grouped together
     */
    @Test
    fun `multiple meals in same dayTime should be grouped together`() = testScope.runTest {
        // Given
        val breakfastMeal1 = testMeal.copy(id = "breakfast1", dayTime = DayTime.BREAKFAST)
        val breakfastMeal2 = testMeal.copy(id = "breakfast2", dayTime = DayTime.BREAKFAST)
        val lunchMeal = testMeal.copy(id = "lunch", dayTime = DayTime.LUNCH)
        val meals = listOf(breakfastMeal1, breakfastMeal2, lunchMeal)

        coEvery { historyRepository.observeMealsForDay(any()) } returns flowOf(meals)

        // When
        viewModel.selectDate(testDate)
        advanceUntilIdle()

        // Then
        val state = viewModel.historyState.value
        assertEquals(2, state.mealsGrouped.size)
        assertEquals(2, state.mealsGrouped[DayTime.BREAKFAST]?.size)
        assertEquals(1, state.mealsGrouped[DayTime.LUNCH]?.size)
    }

    /**
     * Test that calories observation updates both total and goal calories
     */
    @Test
    fun `observeCalories should update both total and goal calories`() = testScope.runTest {
        // Given
        val expectedTotalCalories = 1800
        val expectedGoalCalories = 2200

        coEvery { historyRepository.observeCaloriesOfDay(any()) } returns flowOf(expectedTotalCalories)
        coEvery { userDataRepository.getDailyCalorieGoal() } returns expectedGoalCalories

        // When
        viewModel.selectDate(testDate)
        advanceUntilIdle()

        // Then
        val state = viewModel.historyState.value
        assertEquals(expectedTotalCalories, state.totalCalories)
        assertEquals(expectedGoalCalories, state.goalCalories)
    }

    /**
     * Test that onAddEntryClick method works correctly when called directly
     */
    @Test
    fun `onAddEntryClick should update state and emit event`() = testScope.runTest {
        // When
        viewModel.onAddEntryClick(testDate, DayTime.DINNER)
        advanceUntilIdle()

        // Then - verify state
        val state = viewModel.historyState.value
        assertEquals(testDate, state.selectedDate)
        assertEquals(DayTime.DINNER, state.dayTime)

        // Then - verify event emission
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        val event = events[0] as HistoryEvent.AddEntryClick
        assertEquals(testDate, event.day)
        assertEquals(DayTime.DINNER, event.dayTime)
    }

    /**
     * Test direct method calls for food and recipe clicks
     */
    @Test
    fun `onFoodClicked should emit correct event`() = testScope.runTest {
        // When
        viewModel.onFoodClicked(testMealId, testFoodId)
        advanceUntilIdle()

        // Then
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        val event = events[0] as HistoryEvent.FoodClicked
        assertEquals(testMealId, event.mealId)
        assertEquals(testFoodId, event.foodId)
    }

    /**
     * Test recipe clicked method
     */
    @Test
    fun `onRecipeClicked should emit correct event`() = testScope.runTest {
        // When
        viewModel.onRecipeClicked(testMealId, testRecipeId)
        advanceUntilIdle()

        // Then
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        val event = events[0] as HistoryEvent.RecipeClicked
        assertEquals(testMealId, event.mealId)
        assertEquals(testRecipeId, event.recipeId)
    }

    /**
     * Test details clicked method
     */
    @Test
    fun `onDetailsClick should emit correct event`() = testScope.runTest {
        // When
        viewModel.onDetailsClick(testDetailsId)
        advanceUntilIdle()

        // Then
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        val event = events[0] as HistoryEvent.DetailsClick
        assertEquals(testDetailsId, event.detailsId)
    }

    /**
     * Test direct meal item removal method
     */
    @Test
    fun `onRemoveMealItem should call repository`() = testScope.runTest {
        // When
        viewModel.onRemoveMealItem(testMealFoodItem)
        advanceUntilIdle()

        // Then
        coVerify { historyRepository.removeMealItem(testMealFoodItem) }
    }

    /**
     * Test error handling when repository throws exception during removal
     */
    @Test
    fun `should handle repository exceptions gracefully during meal item removal`() = testScope.runTest {
        // Given
        coEvery { historyRepository.removeMealItem(any()) } throws RuntimeException("Database error")

        // When & Then - should not crash
        try {
            viewModel.onRemoveMealItem(testMealFoodItem)
            advanceUntilIdle()
        } catch (e: Exception) {
            fail("ViewModel should handle repository exceptions gracefully")
        }

        // Verify the repository method was still called
        coVerify { historyRepository.removeMealItem(testMealFoodItem) }
    }

    /**
     * Test error handling when UserDataRepository throws exception
     */
    @Test
    fun `should handle user data repository exceptions gracefully`() = testScope.runTest {
        // Given
        coEvery { userDataRepository.getDailyCalorieGoal() } throws RuntimeException("User data error")

        // When & Then - should not crash the app
        try {
            viewModel.selectDate(testDate)
            advanceUntilIdle()
        } catch (e: Exception) {
            fail("ViewModel should handle user data repository exceptions gracefully")
        }

        // Verify the repository method was called
        coVerify { userDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Test that multiple date selections work correctly
     */
    @Test
    fun `multiple date selections should update state correctly`() = testScope.runTest {
        val firstDate = Date(1704067200000L) // 2024-1-1
        val secondDate = Date(1704153600000L) // 2024-1-2

        // When
        viewModel.selectDate(firstDate)
        advanceUntilIdle()

        val firstState = viewModel.historyState.value
        assertEquals(firstDate, firstState.selectedDate)

        viewModel.selectDate(secondDate)
        advanceUntilIdle()

        val secondState = viewModel.historyState.value
        assertEquals(secondDate, secondState.selectedDate)

        // Verify repository was called for both dates
        coVerify { historyRepository.observeMealsForDay(firstDate) }
        coVerify { historyRepository.observeMealsForDay(secondDate) }
        coVerify { historyRepository.observeCaloriesOfDay(firstDate) }
        coVerify { historyRepository.observeCaloriesOfDay(secondDate) }
    }

    /**
     * Test that empty meals list is handled correctly
     */
    @Test
    fun `empty meals list should result in empty grouped meals`() = testScope.runTest {
        // Given
        coEvery { historyRepository.observeMealsForDay(any()) } returns flowOf(emptyList())
        coEvery { historyRepository.observeCaloriesOfDay(any()) } returns flowOf(0)

        // When
        viewModel.selectDate(testDate)
        advanceUntilIdle()

        // Then
        val state = viewModel.historyState.value
        assertTrue(state.mealsGrouped.isEmpty())
        assertEquals(0, state.totalCalories)
    }

    /**
     * Test that zero calorie goal is handled correctly
     */
    @Test
    fun `zero calorie goal should be handled correctly`() = testScope.runTest {
        // Given
        coEvery { userDataRepository.getDailyCalorieGoal() } returns 0

        // When
        viewModel.selectDate(testDate)
        advanceUntilIdle()

        // Then
        val state = viewModel.historyState.value
        assertEquals(0, state.goalCalories)
        // Should still have total calories from meals
        assertEquals(1500, state.totalCalories)
    }

    /**
     * Test date change triggers distinct repository calls
     */
    @Test
    fun `date changes should trigger distinct repository observations`() = testScope.runTest {
        val date1 = Date(1704067200000L) // 2024-1-1
        val date2 = Date(1704153600000L) // 2024-1-2
        val date3 = date1 // Same as first date

        // When
        viewModel.selectDate(date1)
        advanceUntilIdle()

        viewModel.selectDate(date2)
        advanceUntilIdle()

        viewModel.selectDate(date3)
        advanceUntilIdle()

        // Then - verify each unique date triggers repository calls
        coVerify(atLeast = 2) { historyRepository.observeMealsForDay(date1) }
        coVerify(atLeast = 1) { historyRepository.observeMealsForDay(date2) }
        coVerify(atLeast = 2) { historyRepository.observeCaloriesOfDay(date1) }
        coVerify(atLeast = 1) { historyRepository.observeCaloriesOfDay(date2) }
    }

    /**
     * Test complex meal structure with both food items and recipe items
     */
    @Test
    fun `meals with both food and recipe items should be handled correctly`() = testScope.runTest {
        // Given
        val complexMeal = testMeal.copy(
            mealFoodItems = listOf(testMealFoodItem, testMealFoodItem.copy(mealId = "meal124")),
            mealRecipeItems = listOf(testMealRecipeItem, testMealRecipeItem.copy(mealId = "meal125"))
        )

        coEvery { historyRepository.observeMealsForDay(any()) } returns flowOf(listOf(complexMeal))

        // When
        viewModel.selectDate(testDate)
        advanceUntilIdle()

        // Then
        val state = viewModel.historyState.value
        assertEquals(1, state.mealsGrouped.size)
        val breakfastMeals = state.mealsGrouped[DayTime.BREAKFAST]
        assertNotNull(breakfastMeals)
        assertEquals(1, breakfastMeals?.size)

        val meal = breakfastMeals?.first()
        assertEquals(2, meal?.mealFoodItems?.size)
        assertEquals(2, meal?.mealRecipeItems?.size)
    }

    /**
     * Test state flow collection
     */
    @Test
    fun `historyState flow should emit state changes`() = testScope.runTest {
        // Given
        val newDate = Date(1704153600000L) // 2024-1-2

        // When
        viewModel.selectDate(newDate)
        advanceUntilIdle()

        // Then - collect state and verify
        val states = viewModel.historyState.take(1).toList()
        assertFalse(states.isEmpty())
        assertEquals(newDate, states[0].selectedDate)
    }

    /**
     * Test that events flow emits correctly
     */
    @Test
    fun `events flow should emit events correctly`() = testScope.runTest {
        // When - trigger multiple events
        viewModel.onEvent(HistoryEvent.AddEntryClick(testDate, DayTime.LUNCH))
        advanceUntilIdle()

        // Then - collect events
        val events = viewModel.events.take(1).toList()
        assertEquals(1, events.size)
        assertTrue(events[0] is HistoryEvent.AddEntryClick)
    }

    /**
     * Test nutrition data initialization
     */
    @Test
    fun `nutrition data should initialize as empty map`() = testScope.runTest {
        val state = viewModel.historyState.value
        assertTrue(state.nutritionOfDay.isEmpty())
    }

    /**
     * Test switched flag initialization
     */
    @Test
    fun `switched flag should initialize as false`() = testScope.runTest {
        val state = viewModel.historyState.value
        assertFalse(state.switched)
    }
}