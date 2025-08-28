package com.frontend.nutricheck.client.ui.view_model.dashboard

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view.widgets.WeightRange
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.util.Date

/**
 * Unit test class for WeightHistoryViewModel.
 *
 * Tests weight history retrieval, filtering by date ranges, and state management
 * using mocked repository to ensure proper business logic and UI state handling.
 */
@ExperimentalCoroutinesApi
class WeightHistoryViewModelTest {

    private val mockUserDataRepository = mockk<UserDataRepository>()
    private lateinit var viewModel: WeightHistoryViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    // Sample weight data for testing with different date ranges
    private val currentDate = Date()
    private val oneDayAgo = Date(currentDate.time - 1L * 24 * 60 * 60 * 1000)
    private val oneWeekAgo = Date(currentDate.time - 7L * 24 * 60 * 60 * 1000)
    private val oneMonthAgo = Date(currentDate.time - 35L * 24 * 60 * 60 * 1000)
    private val threeMonthsAgo = Date(currentDate.time - 90L * 24 * 60 * 60 * 1000)
    private val sixMonthsAgo = Date(currentDate.time - 200L * 24 * 60 * 60 * 1000)
    private val oneYearAgo = Date(currentDate.time - 400L * 24 * 60 * 60 * 1000)

    private val sampleWeightData = listOf(
        Weight(75.0, currentDate),
        Weight(74.8, oneDayAgo),
        Weight(75.2, oneWeekAgo),
        Weight(75.5, oneMonthAgo),
        Weight(76.0, threeMonthsAgo),
        Weight(76.5, sixMonthsAgo),
        Weight(77.0, oneYearAgo)
    )

    private val targetWeight = 70.0

    /**
     * Sets up the test environment before each test.
     * Initializes the test dispatcher and creates the ViewModel instance.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeightHistoryViewModel(mockUserDataRepository)
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
     * Tests displayWeightHistory() with LAST_1_MONTH range.
     * Verifies that only weight entries from the last 30 days are included
     * and data is properly sorted by date.
     */
    @Test
    fun `displayWeightHistory filters data for last 1 month correctly`() = runTest {
        coEvery { mockUserDataRepository.getWeightHistory() } returns sampleWeightData
        coEvery { mockUserDataRepository.getTargetWeight() } returns targetWeight

        viewModel.displayWeightHistory(WeightRange.LAST_1_MONTH)

        val state = viewModel.weightHistoryState.first()

        // Should include entries from last 30 days only (current, 1 day, 1 week)
        assertEquals(3, state.weightData.size)
        assertEquals(targetWeight, state.weightGoal, 0.0)

        // Verify entries are sorted by date (oldest first)
        assertTrue(
            state.weightData[0].date.before(state.weightData[1].date) ||
                    state.weightData[0].date == state.weightData[1].date
        )
        assertTrue(
            state.weightData[1].date.before(state.weightData[2].date) ||
                    state.weightData[1].date == state.weightData[2].date
        )

        coVerify { mockUserDataRepository.getWeightHistory() }
        coVerify { mockUserDataRepository.getTargetWeight() }
    }

    /**
     * Tests displayWeightHistory() with LAST_6_MONTHS range.
     * Verifies that weight entries from the last 180 days are included
     * and entries older than 6 months are excluded.
     */
    @Test
    fun `displayWeightHistory filters data for last 6 months correctly`() = runTest {
        coEvery { mockUserDataRepository.getWeightHistory() } returns sampleWeightData
        coEvery { mockUserDataRepository.getTargetWeight() } returns targetWeight

        viewModel.displayWeightHistory(WeightRange.LAST_6_MONTHS)

        val state = viewModel.weightHistoryState.first()

        // Should include entries from last 180 days (current, 1 day, 1 week, 1 month, 3 months)
        assertEquals(5, state.weightData.size)
        assertEquals(targetWeight, state.weightGoal, 0.0)

        // Verify that 6+ months old entries are not included
        val cutoffDate = Date(currentDate.time - 180L * 24 * 60 * 60 * 1000)
        assertTrue(state.weightData.all { it.date.after(cutoffDate) || it.date == cutoffDate })

        coVerify { mockUserDataRepository.getWeightHistory() }
        coVerify { mockUserDataRepository.getTargetWeight() }
    }

    /**
     * Tests displayWeightHistory() with LAST_12_MONTHS range.
     * Verifies that weight entries from the last 365 days are included
     * and all sample data should be present since none exceeds 1 year.
     */
    @Test
    fun `displayWeightHistory filters data for last 12 months correctly`() = runTest {
        coEvery { mockUserDataRepository.getWeightHistory() } returns sampleWeightData
        coEvery { mockUserDataRepository.getTargetWeight() } returns targetWeight

        viewModel.displayWeightHistory(WeightRange.LAST_12_MONTHS)

        val state = viewModel.weightHistoryState.first()

        // Should include entries from last 365 days (current, 1 day, 1 week, 1 month, 3 months, 6 months)
        assertEquals(6, state.weightData.size)
        assertEquals(targetWeight, state.weightGoal, 0.0)

        // Verify that entries older than 1 year are not included
        val cutoffDate = Date(currentDate.time - 365L * 24 * 60 * 60 * 1000)
        assertTrue(state.weightData.all { it.date.after(cutoffDate) || it.date == cutoffDate })

        coVerify { mockUserDataRepository.getWeightHistory() }
        coVerify { mockUserDataRepository.getTargetWeight() }
    }

    /**
     * Tests displayWeightHistory() with empty weight data.
     * Verifies that the ViewModel handles empty data gracefully and still retrieves the target weight.
     */
    @Test
    fun `displayWeightHistory handles empty weight data correctly`() = runTest {
        coEvery { mockUserDataRepository.getWeightHistory() } returns emptyList()
        coEvery { mockUserDataRepository.getTargetWeight() } returns targetWeight

        viewModel.displayWeightHistory(WeightRange.LAST_1_MONTH)

        val state = viewModel.weightHistoryState.first()

        assertEquals(0, state.weightData.size)
        assertEquals(targetWeight, state.weightGoal, 0.0)

        coVerify { mockUserDataRepository.getWeightHistory() }
        coVerify { mockUserDataRepository.getTargetWeight() }
    }



    /**
     * Tests initial state of WeightHistoryState.
     * Verifies that the ViewModel starts with correct default values.
     */
    @Test
    fun `initial state has correct default values`() = runTest {
        val initialState = viewModel.weightHistoryState.first()

        assertEquals(emptyList<Int>(), initialState.weightData)
        assertEquals(0.0, initialState.weightGoal, 0.0)
    }

    /**
     * Tests that weight data is correctly sorted by date in ascending order.
     * Verifies the sorting logic works correctly regardless of input order.
     */
    @Test
    fun `displayWeightHistory sorts weight data by date ascending`() = runTest {
        val unsortedWeightData = listOf(
            Weight(75.0, threeMonthsAgo),
            Weight(74.8, currentDate),
            Weight(75.2, oneWeekAgo),
            Weight(75.5, oneDayAgo)
        )

        coEvery { mockUserDataRepository.getWeightHistory() } returns unsortedWeightData
        coEvery { mockUserDataRepository.getTargetWeight() } returns targetWeight

        viewModel.displayWeightHistory(WeightRange.LAST_6_MONTHS)

        val state = viewModel.weightHistoryState.first()

        // Verify data is sorted by date (oldest first)
        for (i in 0 until state.weightData.size - 1) {
            assertTrue(
                state.weightData[i].date.before(state.weightData[i + 1].date) ||
                        state.weightData[i].date == state.weightData[i + 1].date
            )
        }

        coVerify { mockUserDataRepository.getWeightHistory() }
        coVerify { mockUserDataRepository.getTargetWeight() }
    }

    /**
     * Tests displayWeightHistory() with zero target weight.
     */
    @Test
    fun `displayWeightHistory handles zero target weight correctly`() = runTest {
        coEvery { mockUserDataRepository.getWeightHistory() } returns sampleWeightData
        coEvery { mockUserDataRepository.getTargetWeight() } returns 0.0

        viewModel.displayWeightHistory(WeightRange.LAST_1_MONTH)

        val state = viewModel.weightHistoryState.first()

        assertEquals(0.0, state.weightGoal, 0.0)
        assertTrue(state.weightData.isNotEmpty())

        coVerify { mockUserDataRepository.getWeightHistory() }
        coVerify { mockUserDataRepository.getTargetWeight() }
    }
}