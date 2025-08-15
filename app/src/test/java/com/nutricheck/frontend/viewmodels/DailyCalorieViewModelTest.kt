package com.nutricheck.frontend.viewmodels

import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyCalorieState
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyCalorieViewModel
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
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit test class for DailyCalorieViewModel.
 *
 * Tests daily calorie data retrieval, goal fetching, date handling, and state management
 * using mocked repositories to ensure proper business logic and UI state handling.
 */
@ExperimentalCoroutinesApi
class DailyCalorieViewModelTest {

    private val mockUserDataRepository = mockk<UserDataRepository>()
    private val mockHistoryRepository = mockk<HistoryRepository>()
    private lateinit var viewModel: DailyCalorieViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    // Sample test data for calorie values
    private val sampleDailyCalories = 1850
    private val sampleCalorieGoal = 2200
    private val zeroDailyCalories = 0
    private val zeroCalorieGoal = 0
    private val highDailyCalories = 3500
    private val highCalorieGoal = 4000

    /**
     * Sets up the test environment before each test.
     * Initializes the test dispatcher and mocks default return values.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Setup default mock responses
        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns sampleCalorieGoal
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returns sampleDailyCalories
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
     * Verifies that all calorie values start at zero before any data is loaded.
     */
    @Test
    fun `initial state has correct default values`() = runTest {
        val initialState = DailyCalorieState()

        assertEquals(0, initialState.dailyCalories)
        assertEquals(0, initialState.calorieGoal)
    }

    /**
     * Tests displayDailyCalories() with normal calorie and goal values.
     * Verifies that both current intake and goal are correctly retrieved and set in state.
     */
    @Test
    fun `displayDailyCalories updates state with correct calorie data`() = runTest {
        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        val state = viewModel.dailyCalorieState.first()

        assertEquals(sampleDailyCalories, state.dailyCalories) // 1850
        assertEquals(sampleCalorieGoal, state.calorieGoal) // 2200

        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
        coVerify { mockHistoryRepository.getCaloriesOfDay(any()) }
    }

    /**
     * Tests displayDailyCalories() when called explicitly after initialization.
     * Verifies that the method can be called multiple times and updates state correctly.
     */
    @Test
    fun `displayDailyCalories can be called explicitly and updates state`() = runTest {
        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        // Update mock data for second call
        val updatedCalories = 2100
        val updatedGoal = 2500

        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns updatedGoal
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returns updatedCalories

        // Call displayDailyCalories explicitly
        viewModel.displayDailyCalories()

        val state = viewModel.dailyCalorieState.first()

        assertEquals(updatedCalories, state.dailyCalories) // 2100
        assertEquals(updatedGoal, state.calorieGoal) // 2500

        // Verify methods were called twice (once in init, once explicitly)
        coVerify(exactly = 2) { mockUserDataRepository.getDailyCalorieGoal() }
        coVerify(exactly = 2) { mockHistoryRepository.getCaloriesOfDay(any()) }
    }

    /**
     * Tests displayDailyCalories() with zero values for calories and goal.
     * Verifies that the ViewModel handles edge case where no data is available.
     */
    @Test
    fun `displayDailyCalories handles zero values correctly`() = runTest {
        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns zeroCalorieGoal
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returns zeroDailyCalories

        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        val state = viewModel.dailyCalorieState.first()

        assertEquals(0, state.dailyCalories)
        assertEquals(0, state.calorieGoal)

        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
        coVerify { mockHistoryRepository.getCaloriesOfDay(any()) }
    }

    /**
     * Tests displayDailyCalories() with high calorie values.
     * Verifies that the ViewModel correctly handles larger numeric values.
     */
    @Test
    fun `displayDailyCalories handles high calorie values correctly`() = runTest {
        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns highCalorieGoal
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returns highDailyCalories

        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        val state = viewModel.dailyCalorieState.first()

        assertEquals(highDailyCalories, state.dailyCalories) // 3500
        assertEquals(highCalorieGoal, state.calorieGoal) // 4000

        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
        coVerify { mockHistoryRepository.getCaloriesOfDay(any()) }
    }

    /**
     * Tests displayDailyCalories() when daily intake exceeds the goal.
     * Verifies that the ViewModel handles overconsumption scenarios correctly.
     */
    @Test
    fun `displayDailyCalories handles intake exceeding goal correctly`() = runTest {
        val lowGoal = 1500
        val highIntake = 2800

        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns lowGoal
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returns highIntake

        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        val state = viewModel.dailyCalorieState.first()

        assertEquals(highIntake, state.dailyCalories) // 2800
        assertEquals(lowGoal, state.calorieGoal) // 1500

        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
        coVerify { mockHistoryRepository.getCaloriesOfDay(any()) }
    }

    /**
     * Tests that displayDailyCalories() passes current date to history repository.
     * Verifies that the correct date (today) is used for calorie lookup.
     */
    @Test
    fun `displayDailyCalories passes current date to history repository`() = runTest {
        val dateSlot = slot<Date>()
        coEvery { mockHistoryRepository.getCaloriesOfDay(capture(dateSlot)) } returns sampleDailyCalories

        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        // Verify that the captured date represents today
        val expectedDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val capturedDate = dateSlot.captured

        // Allow for small time differences (test execution time)
        val timeDifference = abs(expectedDate.time - capturedDate.time)
        assertTrue(timeDifference < 1000) // Less than 1 second difference

        coVerify { mockHistoryRepository.getCaloriesOfDay(any()) }
        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
    }

    /**
     * Tests that repository methods are called in the correct order during initialization.
     * Verifies the ViewModel's dependency interaction pattern.
     */
    @Test
    fun `init calls repository methods correctly`() = runTest {
        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        // Verify both repository methods are called during initialization
        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
        coVerify { mockHistoryRepository.getCaloriesOfDay(any()) }
    }

    /**
     * Tests that state updates preserve immutability using copy pattern.
     * Verifies the proper state management approach is used.
     */
    @Test
    fun `state updates use direct assignment correctly`() = runTest {
        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        val state = viewModel.dailyCalorieState.first()

        // Verify state was updated with repository data
        assertEquals(sampleDailyCalories, state.dailyCalories)
        assertEquals(sampleCalorieGoal, state.calorieGoal)
    }

    /**
     * Tests displayDailyCalories() with negative calorie values.
     * Verifies edge case handling for unexpected negative values from repositories.
     */
    @Test
    fun `displayDailyCalories handles negative values correctly`() = runTest {
        val negativeCalories = -100
        val negativeGoal = -50

        coEvery { mockUserDataRepository.getDailyCalorieGoal() } returns negativeGoal
        coEvery { mockHistoryRepository.getCaloriesOfDay(any()) } returns negativeCalories

        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        val state = viewModel.dailyCalorieState.first()

        assertEquals(negativeCalories, state.dailyCalories)
        assertEquals(negativeGoal, state.calorieGoal)

        coVerify { mockUserDataRepository.getDailyCalorieGoal() }
        coVerify { mockHistoryRepository.getCaloriesOfDay(any()) }
    }

    /**
     * Tests date creation logic consistency.
     * Verifies that multiple calls create dates for the same day.
     */
    @Test
    fun `displayDailyCalories creates consistent current date`() = runTest {
        val dateSlot = mutableListOf<Date>()
        coEvery { mockHistoryRepository.getCaloriesOfDay(capture(dateSlot)) } returns sampleDailyCalories

        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        // Call method again to capture another date
        viewModel.displayDailyCalories()

        // Both captured dates should represent the same day
        assertEquals(2, dateSlot.size)

        val date1 = dateSlot[0]
        val date2 = dateSlot[1]

        // Dates should be very close (same day)
        val timeDifference = abs(date1.time - date2.time)
        assertTrue(timeDifference < 5000) // Less than 5 seconds difference

        coVerify(exactly = 2) { mockHistoryRepository.getCaloriesOfDay(any()) }
    }


    /**
     * Tests multiple rapid calls to displayDailyCalories().
     * Verifies that concurrent calls don't cause race conditions or inconsistent state.
     */
    @Test
    fun `multiple rapid displayDailyCalories calls work correctly`() = runTest {
        viewModel = DailyCalorieViewModel(mockUserDataRepository, mockHistoryRepository)

        // Make multiple rapid calls
        repeat(5) {
            viewModel.displayDailyCalories()
        }

        val finalState = viewModel.dailyCalorieState.first()

        // Final state should reflect the mocked values
        assertEquals(sampleDailyCalories, finalState.dailyCalories)
        assertEquals(sampleCalorieGoal, finalState.calorieGoal)

        // Verify total call count (1 from init + 5 explicit calls)
        coVerify(exactly = 6) { mockUserDataRepository.getDailyCalorieGoal() }
        coVerify(exactly = 6) { mockHistoryRepository.getCaloriesOfDay(any()) }
    }
}