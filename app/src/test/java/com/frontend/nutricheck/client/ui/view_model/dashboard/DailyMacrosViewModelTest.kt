package com.frontend.nutricheck.client.ui.view_model.dashboard

import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyMacrosState
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyMacrosViewModel
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
import kotlin.test.assertEquals

/**
 * Unit test class for DailyMacrosViewModel.
 */
@ExperimentalCoroutinesApi
class DailyMacrosViewModelTest {

    private val mockHistoryRepository = mockk<HistoryRepository>()
    private val mockUserDataRepository = mockk<UserDataRepository>()
    private lateinit var viewModel: DailyMacrosViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    // Sample test data for macronutrient goals and daily intake
    private val sampleMacroGoals = listOf(250, 150, 65) // carbs, protein, fat goals
    private val sampleDailyMacros = listOf(200, 120, 50) // carbs, protein, fat intake

    private val highMacroGoals = listOf(400, 200, 100)
    private val highDailyMacros = listOf(350, 180, 85)

    /**
     * Sets up the test environment before each test.
     * Initializes the test dispatcher and mocks default return values.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Setup default mock responses
        coEvery { mockUserDataRepository.getNutrientGoal() } returns sampleMacroGoals
        coEvery { mockHistoryRepository.getDailyMacros() } returns sampleDailyMacros
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
     * Verifies that all macro values start at zero before any data is loaded.
     */
    @Test
    fun `initial state has correct default values`() = runTest {
        // Create ViewModel without triggering init to check initial state
        val initialState = DailyMacrosState()

        assertEquals(0, initialState.dailyProtein)
        assertEquals(0, initialState.dailyProteinGoal)
        assertEquals(0, initialState.dailyCarbs)
        assertEquals(0, initialState.dailyCarbsGoal)
        assertEquals(0, initialState.dailyFat)
        assertEquals(0, initialState.dailyFatGoal)
    }

    /**
     * Tests displayDailyMacros() with normal macro goals and intake values.
     * Verifies that both goals and current intake are correctly mapped to the state.
     */
    @Test
    fun `displayDailyMacros updates state with correct macro goals and intake`() = runTest {
        viewModel = DailyMacrosViewModel(mockHistoryRepository, mockUserDataRepository)

        val state = viewModel.dailyMacrosState.first()

        // Verify macro goals are correctly mapped (carbs[0], protein[1], fat[2])
        assertEquals(sampleMacroGoals[0], state.dailyCarbsGoal) // 250
        assertEquals(sampleMacroGoals[1], state.dailyProteinGoal) // 150
        assertEquals(sampleMacroGoals[2], state.dailyFatGoal) // 65

        // Verify daily intake is correctly mapped (carbs[0], protein[1], fat[2])
        assertEquals(sampleDailyMacros[0], state.dailyCarbs) // 200
        assertEquals(sampleDailyMacros[1], state.dailyProtein) // 120
        assertEquals(sampleDailyMacros[2], state.dailyFat) // 50

        coVerify { mockUserDataRepository.getNutrientGoal() }
        coVerify { mockHistoryRepository.getDailyMacros() }
    }

    /**
     * Tests displayDailyMacros() when called explicitly after initialization.
     * Verifies that the method can be called multiple times and updates state correctly.
     */
    @Test
    fun `displayDailyMacros can be called explicitly and updates state`() = runTest {
        viewModel = DailyMacrosViewModel(mockHistoryRepository, mockUserDataRepository)

        // Update mock data for second call
        val updatedMacroGoals = listOf(300, 180, 80)
        val updatedDailyMacros = listOf(250, 160, 70)

        coEvery { mockUserDataRepository.getNutrientGoal() } returns updatedMacroGoals
        coEvery { mockHistoryRepository.getDailyMacros() } returns updatedDailyMacros

        // Call displayDailyMacros explicitly
        viewModel.displayDailyMacros()

        val state = viewModel.dailyMacrosState.first()

        assertEquals(updatedMacroGoals[0], state.dailyCarbsGoal) // 300
        assertEquals(updatedMacroGoals[1], state.dailyProteinGoal) // 180
        assertEquals(updatedMacroGoals[2], state.dailyFatGoal) // 80
        assertEquals(updatedDailyMacros[0], state.dailyCarbs) // 250
        assertEquals(updatedDailyMacros[1], state.dailyProtein) // 160
        assertEquals(updatedDailyMacros[2], state.dailyFat) // 70

        // Verify methods were called twice (once in init, once explicitly)
        coVerify(exactly = 2) { mockUserDataRepository.getNutrientGoal() }
        coVerify(exactly = 2) { mockHistoryRepository.getDailyMacros() }
    }

    /**
     * Tests displayDailyMacros() with high macro values.
     * Verifies that the ViewModel correctly handles larger numeric values.
     */
    @Test
    fun `displayDailyMacros handles high macro values correctly`() = runTest {
        coEvery { mockUserDataRepository.getNutrientGoal() } returns highMacroGoals
        coEvery { mockHistoryRepository.getDailyMacros() } returns highDailyMacros

        viewModel = DailyMacrosViewModel(mockHistoryRepository, mockUserDataRepository)

        val state = viewModel.dailyMacrosState.first()

        assertEquals(highMacroGoals[0], state.dailyCarbsGoal) // 400
        assertEquals(highMacroGoals[1], state.dailyProteinGoal) // 200
        assertEquals(highMacroGoals[2], state.dailyFatGoal) // 100
        assertEquals(highDailyMacros[0], state.dailyCarbs) // 350
        assertEquals(highDailyMacros[1], state.dailyProtein) // 180
        assertEquals(highDailyMacros[2], state.dailyFat) // 85

        coVerify { mockUserDataRepository.getNutrientGoal() }
        coVerify { mockHistoryRepository.getDailyMacros() }
    }

    /**
     * Tests displayDailyMacros() with mismatched goal and intake values.
     * Verifies that intake can be higher or lower than goals without issues.
     */
    @Test
    fun `displayDailyMacros handles intake exceeding goals correctly`() = runTest {
        val lowGoals = listOf(100, 80, 30)
        val highIntake = listOf(150, 120, 50)

        coEvery { mockUserDataRepository.getNutrientGoal() } returns lowGoals
        coEvery { mockHistoryRepository.getDailyMacros() } returns highIntake

        viewModel = DailyMacrosViewModel(mockHistoryRepository, mockUserDataRepository)

        val state = viewModel.dailyMacrosState.first()

        // Goals should be set to low values
        assertEquals(100, state.dailyCarbsGoal)
        assertEquals(80, state.dailyProteinGoal)
        assertEquals(30, state.dailyFatGoal)

        // Intake should be set to high values (exceeding goals)
        assertEquals(150, state.dailyCarbs)
        assertEquals(120, state.dailyProtein)
        assertEquals(50, state.dailyFat)

        coVerify { mockUserDataRepository.getNutrientGoal() }
        coVerify { mockHistoryRepository.getDailyMacros() }
    }

    /**
     * Tests that repository methods are called in the correct order during initialization.
     * Verifies the ViewModel's dependency interaction pattern.
     */
    @Test
    fun `init calls repository methods correctly`() = runTest {
        viewModel = DailyMacrosViewModel(mockHistoryRepository, mockUserDataRepository)

        // Verify both repository methods are called during initialization
        coVerify { mockUserDataRepository.getNutrientGoal() }
        coVerify { mockHistoryRepository.getDailyMacros() }
    }

    /**
     * Tests that state updates preserve other values when using copy().
     * Verifies the immutable state update pattern works correctly.
     */
    @Test
    fun `state updates use copy pattern correctly`() = runTest {
        viewModel = DailyMacrosViewModel(mockHistoryRepository, mockUserDataRepository)

        val initialState = viewModel.dailyMacrosState.first()

        // All values should be updated from the repositories
        assertEquals(sampleMacroGoals[0], initialState.dailyCarbsGoal)
        assertEquals(sampleMacroGoals[1], initialState.dailyProteinGoal)
        assertEquals(sampleMacroGoals[2], initialState.dailyFatGoal)
        assertEquals(sampleDailyMacros[0], initialState.dailyCarbs)
        assertEquals(sampleDailyMacros[1], initialState.dailyProtein)
        assertEquals(sampleDailyMacros[2], initialState.dailyFat)
    }
}