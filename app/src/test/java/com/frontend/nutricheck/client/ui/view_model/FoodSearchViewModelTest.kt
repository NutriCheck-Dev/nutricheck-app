@file:OptIn(ExperimentalCoroutinesApi::class)

package com.frontend.nutricheck.client.ui.view_model

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.Language
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.snackbar.SnackbarManager
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

class FoodSearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var appSettings: AppSettingRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var foodProductRepository: FoodProductRepository
    private lateinit var historyRepository: HistoryRepository
    private lateinit var snackbarManager: SnackbarManager
    private lateinit var combinedStore: CombinedSearchListStore
    private lateinit var context: Context
    private val foodProduct1 = FoodProduct(
        id = "fp1",
        name = "Pasta",
        calories = 200.0,
        carbohydrates = 30.0,
        protein = 10.0,
        fat = 5.0,
        servings = 1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    private val foodProduct2 = FoodProduct(
        id = "fp2",
        name = "Pesto Sauce",
        calories = 100.0,
        carbohydrates = 10.0,
        protein = 5.0,
        fat = 5.0,
        servings = 1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    private val recipe1 = Recipe(
        id = "r1",
        name = "Pasta Pesto",
        instructions = "Boil water",
        calories = 300.0, carbohydrates = 40.0, protein = 15.0, fat = 10.0,
        servings = 1,
        ingredients = listOf(
            Ingredient(
                recipeId = "r1",
                foodProduct = foodProduct1,
                servings = 1,
                servingSize = foodProduct1.servingSize
            ),
            Ingredient(
                recipeId = "r1",
                foodProduct = foodProduct2,
                servings = 1,
                servingSize = foodProduct2.servingSize
            )
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        appSettings = mockk()
        recipeRepository = mockk()
        foodProductRepository = mockk()
        historyRepository = mockk()
        snackbarManager = mockk(relaxed = true)
        combinedStore = mockk(relaxed = true)
        context = mockk()

        every { appSettings.language } returns flowOf(Language.GERMAN)
        every { context.getString(any()) } returns "Test String"
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun makeViewModel(handle: SavedStateHandle = SavedStateHandle()): FoodSearchViewModel =
        FoodSearchViewModel(
            appSettings,
            recipeRepository,
            foodProductRepository,
            historyRepository,
            combinedStore,
            snackbarManager,
            context,
            handle
        )

    @Test
    fun `init sets language and optional dayTime-date`() = runTest {
        val languageFlow = MutableStateFlow(Language.GERMAN)
        every { appSettings.language } returns languageFlow
        val handle = SavedStateHandle(
            mapOf(
                "mealId" to "meal1",
                "dayTime" to DayTime.BREAKFAST.name,
                "date" to "1690000000000"
            )
        )
        val searchViewModel = makeViewModel(handle)
        advanceUntilIdle()

        val state = searchViewModel.searchState.value as SearchUiState.AddComponentsToMealState
        assertEquals("de", state.parameters.language)
        assertEquals(DayTime.BREAKFAST, state.dayTime)
        assertEquals(1690000000000L, state.date)
    }

    @Test
    fun `QueryChanged updates query, ExpandBottomSheet and MealSelectorClick toggle flags`() = runTest {
        val searchViewModel = makeViewModel()
        advanceUntilIdle()

        searchViewModel.onEvent(SearchEvent.QueryChanged("pa"))
        advanceUntilIdle()
        var state = searchViewModel.searchState.value
        assertEquals("pa", state.parameters.query)

        searchViewModel.onEvent(SearchEvent.ShowBottomSheet)
        advanceUntilIdle()
        state = searchViewModel.searchState.value
        assertTrue(state.parameters.bottomSheetExpanded)

        searchViewModel.onEvent(SearchEvent.MealSelectorClick)
        advanceUntilIdle()
        state = searchViewModel.searchState.value
        assertTrue(state.parameters.expanded)
    }

    @Test
    fun `Search on All tab merges foodProducts and recipes`() = runTest {
        coEvery { foodProductRepository.searchFoodProducts("pasta", any())
        } returns flowOf(Result.Success(listOf(foodProduct1)))
        coEvery { recipeRepository.searchRecipes("pasta")
        } returns flowOf(Result.Success(listOf(recipe1)))


        val searchViewModel = makeViewModel()
        advanceUntilIdle()
        searchViewModel.onEvent(SearchEvent.QueryChanged("pasta"))
        searchViewModel.onEvent(SearchEvent.Search)
        advanceUntilIdle()

        val state = searchViewModel.searchState.value
        val results = state.parameters.generalResults
        assertEquals(setOf("fp1", "r1"), results.map { it.id }.toSet())

        verify(atLeast = 1) { combinedStore.update(any()) }
    }

    @Test
    fun `Search on All tab - upstream exception sets error ui state`() = runTest {
        coEvery {
            foodProductRepository.searchFoodProducts("boom", any())
        } returns flow {
            emit(Result.Success(emptyList()))
            throw RuntimeException("boom")
        }
        coEvery { recipeRepository.searchRecipes("boom") } returns emptyFlow()

        val searchViewModel = makeViewModel()
        advanceUntilIdle()
        searchViewModel.onEvent(SearchEvent.QueryChanged("boom"))
        searchViewModel.onEvent(SearchEvent.Search)
        advanceUntilIdle()

        assertTrue(searchViewModel.uiState.value is BaseViewModel.UiState.Error)
    }

    @Test
    fun `Search on MyRecipes tab filters local recipes and updates combined store`() = runTest {
        coEvery { recipeRepository.observeMyRecipes() } returns flowOf(listOf(recipe1))

        val searchViewModel = makeViewModel()
        advanceUntilIdle()

        searchViewModel.onEvent(SearchEvent.ClickSearchMyRecipes)
        assertEquals(1, searchViewModel.searchState.value.parameters.selectedTab)

        searchViewModel.onEvent(SearchEvent.QueryChanged("pasta"))
        searchViewModel.onEvent(SearchEvent.Search)
        advanceUntilIdle()

        val state = searchViewModel.searchState.value
        assertEquals(listOf("r1"), state.parameters.localRecipesResults.map { it.id })

        verify { combinedStore.update(
            match { it.any { component -> component.id == "r1" } }
        ) }
    }

    @Test
    fun `AddFoodComponent moves item from results to added and emits event`() = runTest {
        coEvery { foodProductRepository.searchFoodProducts("pasta", "de") } returns flowOf(Result.Success(listOf(foodProduct1)))
        coEvery { recipeRepository.searchRecipes("pasta") } returns flowOf(Result.Success(emptyList()))

        val searchViewModel = makeViewModel()
        advanceUntilIdle()
        searchViewModel.onEvent(SearchEvent.QueryChanged("pasta"))
        searchViewModel.onEvent(SearchEvent.Search)
        advanceUntilIdle()

        val awaited = async { searchViewModel.events.first() }
        searchViewModel.onEvent(SearchEvent.AddFoodComponent(foodProduct1))
        advanceUntilIdle()

        val state = searchViewModel.searchState.value
        assertTrue(state.parameters.addedComponents.any { it.id == "fp1" })
        assertTrue(state.parameters.generalResults.none { it.id == "fp1" })
        assertTrue(awaited.await() is SearchEvent.AddFoodComponent)

        verify(atLeast = 1) { combinedStore.update(any()) }
    }

    @Test
    fun `AddFoodComponent twice for FoodProduct triggers replacement`() = runTest {
        every { appSettings.language } returns flowOf(Language.GERMAN)

        val searchViewModel = makeViewModel()
        advanceUntilIdle()

        searchViewModel.onEvent(SearchEvent.AddFoodComponent(foodProduct1))
        advanceUntilIdle()

        val updated = foodProduct1.copy(servings = 2)
        searchViewModel.onEvent(SearchEvent.AddFoodComponent(updated))
        advanceUntilIdle()

        val state = searchViewModel.searchState.value
        val added = state.parameters.addedComponents.single { it.id == "fp1" } as FoodProduct
        assertEquals(2, added.servings)
        verify(atLeast = 1) { combinedStore.update(any()) }
    }

    @Test
    fun `AddFoodComponent twice for Recipe triggers replacement`() = runTest {
        every { appSettings.language } returns flowOf(Language.GERMAN)

        val searchViewModel = makeViewModel()
        advanceUntilIdle()

        searchViewModel.onEvent(SearchEvent.AddFoodComponent(recipe1))
        advanceUntilIdle()

        val changed = recipe1.copy(servings = 3)
        searchViewModel.onEvent(SearchEvent.AddFoodComponent(changed))
        advanceUntilIdle()

        val state = searchViewModel.searchState.value
        val added = state.parameters.addedComponents.single { it.id == "r1" } as Recipe
        assertEquals(3, added.servings)
        assertEquals(recipe1.ingredients.size, added.ingredients.size)
        verify(atLeast = 1) { combinedStore.update(any()) }
    }

    @Test
    fun `RemoveFoodComponent moves item back to results`() = runTest {
        coEvery { foodProductRepository.searchFoodProducts("pasta", "de") } returns flowOf(Result.Success(listOf(foodProduct1)))
        coEvery { recipeRepository.searchRecipes("pasta") } returns flowOf(Result.Success(emptyList()))

        val searchViewModel = makeViewModel()
        advanceUntilIdle()
        searchViewModel.onEvent(SearchEvent.QueryChanged("pasta"))
        searchViewModel.onEvent(SearchEvent.Search)
        advanceUntilIdle()

        searchViewModel.onEvent(SearchEvent.AddFoodComponent(foodProduct1))
        advanceUntilIdle()
        searchViewModel.onEvent(SearchEvent.RemoveFoodComponent(foodProduct1))
        advanceUntilIdle()

        val state = searchViewModel.searchState.value
        assertTrue(state.parameters.addedComponents.none { it.id == "fp1" })
        assertTrue(state.parameters.generalResults.any { it.id == "fp1" })
        verify(atLeast = 1) { combinedStore.update(any()) }
    }

    @Test
    fun `Clear resets query and results`() = runTest {
        val searchViewModel = makeViewModel()
        searchViewModel.onEvent(SearchEvent.QueryChanged("x"))
        advanceUntilIdle()

        searchViewModel.onEvent(SearchEvent.Clear)
        advanceUntilIdle()

        val state = searchViewModel.searchState.value
        assertEquals("", state.parameters.query)
        assertTrue(state.parameters.generalResults.isEmpty())
        assertTrue(state.parameters.localRecipesResults.isEmpty())
        assertEquals(null, state.parameters.lastSearchedQuery)
        verify { combinedStore.update(emptyList()) }
    }

    @Test
    fun `SubmitComponentsToMeal without daytime sets error`() = runTest {
        val searchViewModel = makeViewModel()
        searchViewModel.onEvent(SearchEvent.SubmitComponentsToMeal)
        advanceUntilIdle()

        assertTrue(searchViewModel.uiState.value is BaseViewModel.UiState.Error)
        coVerify(exactly = 0) { historyRepository.addMeal(any()) }
        coVerify(exactly = 0) { historyRepository.updateMeal(any()) }
    }

    @Test
    fun `SubmitComponentsToMeal with items in LogNewMeal adds meal and emits MealsSaved`() = runTest {
        coEvery { historyRepository.addMeal(any()) } just Runs

        val searchViewModel = makeViewModel()
        searchViewModel.onEvent(SearchEvent.AddFoodComponent(foodProduct1))
        searchViewModel.onEvent(SearchEvent.AddFoodComponent(recipe1))
        searchViewModel.onEvent(SearchEvent.DayTimeChanged(DayTime.LUNCH))
        advanceUntilIdle()

        val awaited = async { searchViewModel.events.first() }
        searchViewModel.onEvent(SearchEvent.SubmitComponentsToMeal)
        advanceUntilIdle()

        coVerify { historyRepository.addMeal(match { meal ->
            meal.mealFoodItems.any { it.foodProduct.id == "fp1" } &&
                    meal.mealRecipeItems.any { it.recipe.id == "r1" } &&
                    meal.dayTime == DayTime.LUNCH
        }) }

        assertTrue(awaited.await() is SearchEvent.MealSaved)
        assertTrue(searchViewModel.uiState.value is BaseViewModel.UiState.Ready)
    }

    @Test
    fun `SubmitComponentsToMeal in ComponentsForMeal updates existing meal`() = runTest {
        val existing = Meal(
            id = "m1", calories = 0.0, carbohydrates = 0.0, protein = 0.0, fat = 0.0,
            date = Date(), dayTime = DayTime.BREAKFAST,
            mealFoodItems = emptyList(), mealRecipeItems = emptyList()
        )
        coEvery { historyRepository.getMealById("m1") } returns existing
        coEvery { historyRepository.updateMeal(any()) } just Runs

        val handle = SavedStateHandle(mapOf("mealId" to "m1"))
        val searchViewModel = makeViewModel(handle)
        advanceUntilIdle()

        searchViewModel.onEvent(SearchEvent.AddFoodComponent(foodProduct1))
        searchViewModel.onEvent(SearchEvent.DayTimeChanged(DayTime.DINNER))
        advanceUntilIdle()

        searchViewModel.onEvent(SearchEvent.SubmitComponentsToMeal)
        advanceUntilIdle()

        coVerify {
            historyRepository.updateMeal(match { meal ->
                meal.id == "m1" &&
                        meal.mealFoodItems.any { it.foodProduct.id == "fp1" }
            })
        }

        assertTrue(searchViewModel.uiState.value is BaseViewModel.UiState.Ready)
    }

    @Test
    fun `ClickSearchMyRecipes and ClickSearchAll switch selectedTab`() = runTest {
        val searchViewModel = makeViewModel()
        searchViewModel.onEvent(SearchEvent.ClickSearchMyRecipes)
        advanceUntilIdle()
        assertEquals(1, searchViewModel.searchState.value.parameters.selectedTab)

        searchViewModel.onEvent(SearchEvent.ClickSearchAll)
        advanceUntilIdle()
        assertEquals(0, searchViewModel.searchState.value.parameters.selectedTab)
    }

    @Test
    fun `ResetErrorState moves uiState to Ready`() = runTest {
        val searchViewModel = makeViewModel()
        searchViewModel.onEvent(SearchEvent.SubmitComponentsToMeal)
        advanceUntilIdle()
        assertTrue(searchViewModel.uiState.value is BaseViewModel.UiState.Error)

        searchViewModel.onEvent(SearchEvent.ResetErrorState)
        advanceUntilIdle()
        assertTrue(searchViewModel.uiState.value is BaseViewModel.UiState.Ready)
    }
}