@file:OptIn(ExperimentalCoroutinesApi::class)

package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistoryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)

    private lateinit var historyRepository: HistoryRepository
    private lateinit var userDataRepository: UserDataRepository

    private lateinit var vm: HistoryViewModel

    private lateinit var mealsFlow: MutableSharedFlow<List<Meal>>
    private lateinit var caloriesFlow: MutableSharedFlow<Int>

    private val dayA = Date(1_000_000L)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        historyRepository = mockk(relaxed = true)
        userDataRepository = mockk(relaxed = true)

        mealsFlow = MutableSharedFlow(replay = 0, extraBufferCapacity = 16)
        caloriesFlow = MutableSharedFlow(replay = 0, extraBufferCapacity = 16)

        coEvery { historyRepository.observeMealsForDay(any()) } returns mealsFlow
        coEvery { historyRepository.observeCaloriesOfDay(any()) } returns caloriesFlow
        coEvery { userDataRepository.getDailyCalorieGoal() } returns 2000

        vm = HistoryViewModel(historyRepository, userDataRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeMeal(id: String, dayTime: DayTime, date: Date, cals: Double = 0.0): Meal =
        Meal(
            id = id,
            calories = cals,
            carbohydrates = 0.0,
            protein = 0.0,
            fat = 0.0,
            date = date,
            dayTime = dayTime,
            mealFoodItems = emptyList(),
            mealRecipeItems = emptyList()
        )

    @Test
    fun `selectDate updates state only`() = testScope.runTest {
        val s0 = vm.historyState.first()
        assertTrue(s0.mealsGrouped.isEmpty())

        vm.selectDate(dayA)
        advanceUntilIdle()

        val s1 = vm.historyState.first()
        assertEquals(dayA, s1.selectedDate)
    }

    @Test
    fun `observeMeals groups meals by DayTime`() = testScope.runTest {
        vm.selectDate(dayA)
        advanceUntilIdle()

        val breakfast = makeMeal("m1", DayTime.BREAKFAST, dayA)
        val dinner = makeMeal("m2", DayTime.DINNER, dayA, cals = 500.0)

        mealsFlow.emit(listOf(breakfast, dinner))
        advanceUntilIdle()

        val s = vm.historyState.first()
        assertEquals(listOf(breakfast), s.mealsGrouped[DayTime.BREAKFAST])
        assertEquals(listOf(dinner), s.mealsGrouped[DayTime.DINNER])
    }

    @Test
    fun `observeCalories updates totals and goal`() = testScope.runTest {
        vm.selectDate(dayA)
        advanceUntilIdle()

        caloriesFlow.emit(950)
        advanceUntilIdle()

        val s = vm.historyState.first()
        assertEquals(950, s.totalCalories)
        assertEquals(2000, s.goalCalories)
    }

    @Test
    fun `onAddEntryClick updates state and emits event`() = testScope.runTest {
        val targetDate = Date(2_000_000L)
        val targetTime = DayTime.SNACK

        val awaited = async { vm.events.first() }

        vm.onAddEntryClick(targetDate, targetTime)
        advanceUntilIdle()

        val s = vm.historyState.first()
        assertEquals(targetDate, s.selectedDate)
        assertEquals(targetTime, s.dayTime)

        val ev = awaited.await()
        assertTrue(ev is HistoryEvent.AddEntryClick)
        assertEquals(targetDate, ev.day)
        assertEquals(targetTime, ev.dayTime)
    }

    @Test
    fun `onFoodClicked emits FoodClicked`() = testScope.runTest {
        val awaited = async { vm.events.first() }
        vm.onFoodClicked("meal-1", "food-9")
        advanceUntilIdle()
        val ev = awaited.await()
        assertTrue(ev is HistoryEvent.FoodClicked)
        assertEquals("meal-1", ev.mealId)
        assertEquals("food-9", ev.foodId)
    }

    @Test
    fun `onRecipeClicked emits RecipeClicked`() = testScope.runTest {
        val awaited = async { vm.events.first() }
        vm.onRecipeClicked("meal-2", "recipe-7")
        advanceUntilIdle()
        val ev = awaited.await()
        assertTrue(ev is HistoryEvent.RecipeClicked)
    }

    @Test
    fun `onDetailsClick emits DetailsClick`() = testScope.runTest {
        val awaited = async { vm.events.first() }
        vm.onDetailsClick("detail-xyz")
        advanceUntilIdle()
        val ev = awaited.await()
        assertTrue(ev is HistoryEvent.DetailsClick)
        assertEquals("detail-xyz", ev.detailsId)
    }

    @Test
    fun `onRemoveMealItem delegates to repository`() = testScope.runTest {
        val item = mockk<MealItem>(relaxed = true)
        vm.onRemoveMealItem(item)
        advanceUntilIdle()
        coVerify(exactly = 1) { historyRepository.removeMealItem(item) }
    }

    @Test
    fun `flatMapLatest switches when date changes`() = testScope.runTest {
        val dayB = Date(3_000_000L)

        val mealsA = MutableSharedFlow<List<Meal>>(extraBufferCapacity = 16)
        val mealsB = MutableSharedFlow<List<Meal>>(extraBufferCapacity = 16)
        val caloriesA = MutableSharedFlow<Int>(extraBufferCapacity = 16)
        val caloriesB = MutableSharedFlow<Int>(extraBufferCapacity = 16)

        coEvery { historyRepository.observeMealsForDay(dayA) } returns mealsA
        coEvery { historyRepository.observeMealsForDay(dayB) } returns mealsB
        coEvery { historyRepository.observeCaloriesOfDay(dayA) } returns caloriesA
        coEvery { historyRepository.observeCaloriesOfDay(dayB) } returns caloriesB

        vm.selectDate(dayA)
        advanceUntilIdle()

        mealsA.tryEmit(listOf(makeMeal("A1", DayTime.LUNCH, dayA)))
        caloriesA.tryEmit(400)
        advanceUntilIdle()
        var s = vm.historyState.first()
        assertEquals(400, s.totalCalories)
        assertTrue(s.mealsGrouped.containsKey(DayTime.LUNCH))

        vm.selectDate(dayB)
        advanceUntilIdle()

        mealsB.tryEmit(listOf(makeMeal("B1", DayTime.DINNER, dayB)))
        caloriesB.tryEmit(1200)
        advanceUntilIdle()

        s = vm.historyState.first()
        assertEquals(1200, s.totalCalories)
        assertTrue(s.mealsGrouped.containsKey(DayTime.DINNER))
    }

    @Test
    fun `goal calories suspend fun verified`() = testScope.runTest {
        vm.selectDate(dayA)
        advanceUntilIdle()

        caloriesFlow.emit(100)
        caloriesFlow.emit(200)
        advanceUntilIdle()

        coVerify(atLeast = 1) { userDataRepository.getDailyCalorieGoal() }
        val s = vm.historyState.first()
        assertEquals(200, s.totalCalories)
        assertEquals(2000, s.goalCalories)
    }
}
