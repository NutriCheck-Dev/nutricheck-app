package com.nutricheck.frontend.viewmodels.dashboard

import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.dashboard.CalorieHistoryEvent
import com.frontend.nutricheck.client.ui.view_model.dashboard.CalorieHistoryViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Date
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit test class for CalorieHistoryViewModel.
 */
@ExperimentalCoroutinesApi
class CalorieHistoryViewModelTest {

    private val mockHistoryRepository = mockk<HistoryRepository>()
    private val mockUserDataRepository = mockk<UserDataRepository>()
    private lateinit var viewModel: CalorieHistoryViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    // Sample test data
    private val sampleCalorieGoal = 2000
    private val sampleCalorieHistory = listOf(1800, 2100, 1950, 2200, 1750)
    private val zeroCalorieGoal = 0

    /**
     * Sets up the test environment before each test.
     * Initializes the test dispatcher and mocks default return values.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Setup default mock responses
        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns sampleCalorieGoal
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany sampleCalorieHistory

        viewModel = CalorieHistoryViewModel(mockHistoryRepository, mockUserDataRepository)
    }

    /**
     * Cleans up the test environment after each test.
     * Resets the main dispatcher to avoid interference between tests.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests that the ViewModel initializes with correct default state values.
     * Verifies that the initial state has empty calorie history and zero goal.
     */
    @Test
    fun `initial state has correct default values`() = runTest {
        val initialState = viewModel.calorieHistoryState.first()

        assertEquals(emptyList(), initialState.calorieHistory)
        assertEquals(0, initialState.calorieGoal)
    }

    /**
     * Verifies that calorie history is retrieved for the correct number of days
     * and state is updated properly.
     */
    @Test
    fun `displayCalorieHistory fetches correct number of days`() = runTest {
        val days = 7
        val expectedCalories = listOf(1800, 2100, 1950, 2200, 1750, 1900, 1890)

        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany expectedCalories

        viewModel.displayCalorieHistory(days)

        val state = viewModel.calorieHistoryState.first()

        assertEquals(expectedCalories, state.calorieHistory)
        assertEquals(sampleCalorieGoal, state.calorieGoal)

        // Verify repository was called for each day
        coVerify(exactly = days) { mockHistoryRepository.getCaloriesOfDay(any()) }
        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
    }
    /**
     * Tests displayCalorieHistory() with a large number of days.
     * Verifies that the method can handle extended date ranges.
     */
    @Test
    fun `displayCalorieHistory handles large number of days`() = runTest {
        val days = 30
        val expectedCalories = (1..30).map { 1500 + (it * 10) } // Varying calorie values

        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany expectedCalories

        viewModel.displayCalorieHistory(days)

        val state = viewModel.calorieHistoryState.first()

        assertEquals(expectedCalories, state.calorieHistory)
        assertEquals(sampleCalorieGoal, state.calorieGoal)

        coVerify(exactly = days) { mockHistoryRepository.getCaloriesOfDay(any()) }
        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Tests onEvent() with DisplayCalorieHistory event.
     * Verifies that events are properly handled and routed to the correct method.
     */
    @Test
    fun `onEvent handles DisplayCalorieHistory event correctly`() = runTest {
        val days = 7
        val expectedCalories = (1..7).map { 2000 + (it * 50) }

        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany expectedCalories

        val event = CalorieHistoryEvent.DisplayCalorieHistory(days)
        viewModel.onEvent(event)

        val state = viewModel.calorieHistoryState.first()

        assertEquals(expectedCalories, state.calorieHistory)
        assertEquals(sampleCalorieGoal, state.calorieGoal)

        coVerify(exactly = days) { mockHistoryRepository.getCaloriesOfDay(any()) }
        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Tests that date range generation creates dates in chronological order.
     * Verifies that the oldest date comes first and newest date comes last.
     */
    @Test
    fun `displayCalorieHistory generates correct date range order`() = runTest {
        val days = 3
        val dateSlot = mutableListOf<Date>()

        coEvery { mockHistoryRepository.getCaloriesOfDay(capture(dateSlot)) } returns 1500

        viewModel.displayCalorieHistory(days)

        // Verify we captured the expected number of dates
        assertEquals(days, dateSlot.size)

        // Verify dates are in chronological order (oldest first)
        for (i in 0 until dateSlot.size - 1) {
            assertTrue(
                dateSlot[i].before(dateSlot[i + 1]) || dateSlot[i] == dateSlot[i + 1],
                "Date at index $i should be before or equal to date at index ${i + 1}"
            )
        }

        // Verify the last date is today (or very close to today)
        val today = Calendar.getInstance().time
        val lastDate = dateSlot.last()
        val timeDifference = abs(today.time - lastDate.time)
        assertTrue(timeDifference < 24 * 60 * 60 * 1000) // Within 24 hours
    }

    /**
     * Tests displayCalorieHistory() with zero calorie goal.
     * Verifies edge case handling when no calorie goal is set.
     */
    @Test
    fun `displayCalorieHistory handles zero calorie goal`() = runTest {
        val days = 3
        val expectedCalories = listOf(1000, 1200, 1100)

        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns zeroCalorieGoal
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany expectedCalories

        viewModel.displayCalorieHistory(days)

        val state = viewModel.calorieHistoryState.first()

        assertEquals(expectedCalories, state.calorieHistory)
        assertEquals(0, state.calorieGoal)

        coVerify(exactly = days) { mockHistoryRepository.getCaloriesOfDay(any()) }
        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Tests displayCalorieHistory() when history repository returns zero calories.
     * Verifies handling of days with no calorie intake.
     */
    @Test
    fun `displayCalorieHistory handles zero calorie days`() = runTest {
        val days = 4
        val expectedCalories = listOf(0, 1500, 0, 2000)

        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany expectedCalories

        viewModel.displayCalorieHistory(days)

        val state = viewModel.calorieHistoryState.first()

        assertEquals(expectedCalories, state.calorieHistory)
        assertEquals(sampleCalorieGoal, state.calorieGoal)

        coVerify(exactly = days) { mockHistoryRepository.getCaloriesOfDay(any()) }
        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Tests multiple calls to displayCalorieHistory().
     * Verifies that subsequent calls update the state correctly.
     */
    @Test
    fun `multiple displayCalorieHistory calls update state correctly`() = runTest {
        // First call with 3 days
        val firstDays = 3
        val firstCalories = listOf(1800, 1900, 2000)
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany firstCalories

        viewModel.displayCalorieHistory(firstDays)
        val firstState = viewModel.calorieHistoryState.first()

        assertEquals(firstCalories, firstState.calorieHistory)

        // Second call with 5 days
        val secondDays = 5
        val secondCalories = listOf(1700, 1800, 1900, 2000, 2100)
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany secondCalories

        viewModel.displayCalorieHistory(secondDays)
        val secondState = viewModel.calorieHistoryState.first()

        assertEquals(secondCalories, secondState.calorieHistory)

        // Verify total call counts
        coVerify(exactly = firstDays + secondDays) { mockHistoryRepository.getCaloriesOfDay(any()) }
        coVerify(exactly = 2) { mockUserDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Tests that date range includes today as the most recent date.
     * Verifies the date range generation logic for current date inclusion.
     */
    @Test
    fun `displayCalorieHistory includes today as most recent date`() = runTest {
        val days = 1
        val dateSlot = slot<Date>()

        coEvery { mockHistoryRepository.getCaloriesOfDay(capture(dateSlot)) } returns 1800

        viewModel.displayCalorieHistory(days)

        val capturedDate = dateSlot.captured
        val today = Calendar.getInstance().time

        // The captured date should be today (within reasonable tolerance)
        val timeDifference = abs(today.time - capturedDate.time)
        assertTrue(timeDifference < 60 * 60 * 1000) // Within 1 hour
    }

    /**
     * Tests displayCalorieHistory() with negative days input.
     * Documents behavior for invalid input values.
     */
    @Test
    fun `displayCalorieHistory handles negative days input`() = runTest {
        val days = -5

        viewModel.displayCalorieHistory(days)

        val state = viewModel.calorieHistoryState.first()

        // With negative days, no history calls should be made
        assertEquals(emptyList(), state.calorieHistory)
        assertEquals(sampleCalorieGoal, state.calorieGoal)

        coVerify(exactly = 0) { mockHistoryRepository.getCaloriesOfDay(any()) }
        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Tests state update mechanism using copy() pattern.
     * Verifies that state updates preserve immutability.
     */
    @Test
    fun `state updates preserve immutability using copy pattern`() = runTest {
        val days = 2
        val expectedCalories = listOf(1600, 1700)
        val expectedGoal = 2500

        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returnsMany expectedCalories
        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns expectedGoal

        viewModel.displayCalorieHistory(days)

        val state = viewModel.calorieHistoryState.first()

        // Verify both fields are updated correctly
        assertEquals(expectedCalories, state.calorieHistory)
        assertEquals(expectedGoal, state.calorieGoal)
    }
}

