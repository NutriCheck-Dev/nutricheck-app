package com.frontend.nutricheck.client.ui.view_model.recipe

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.utils.CombinedSearchListStore
import com.frontend.nutricheck.client.ui.view_model.snackbar.SnackbarManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeOverviewViewModelTest {

    private lateinit var recipeRepository: RecipeRepository
    private lateinit var historyRepository: HistoryRepository
    private lateinit var snackbarManager: SnackbarManager
    private lateinit var combinedSearchListStore: CombinedSearchListStore
    private lateinit var context: Context

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // Test data - Recipe
    private val testRecipe = Recipe(
        id = "r1",
        name = "Pasta Pesto",
        calories = 300.0,
        carbohydrates = 40.0,
        protein = 15.0,
        fat = 10.0,
        servings = 2.0,
        ingredients = listOf(
            Ingredient(
                recipeId = "r1",
                foodProduct = FoodProduct(
                    id = "fp1",
                    name = "Pasta",
                    calories = 200.0,
                    carbohydrates = 30.0,
                    protein = 10.0,
                    fat = 5.0,
                    servings = 1.0,
                    servingSize = ServingSize.ONEHOUNDREDGRAMS
                ),
                quantity = 100.0,
                servings = 1.0,
                servingSize = ServingSize.ONEHOUNDREDGRAMS
            ),
            Ingredient(
                recipeId = "r1",
                foodProduct = FoodProduct(
                    id = "fp2",
                    name = "Pesto Sauce",
                    calories = 100.0,
                    carbohydrates = 10.0,
                    protein = 5.0,
                    fat = 5.0,
                    servings = 1.0,
                    servingSize = ServingSize.ONEHOUNDREDGRAMS
                ),
                quantity = 50.0,
                servings = 5.0,
                servingSize = ServingSize.TENGRAMS
            )
        )
    )

    private fun makeViewModel(handle: SavedStateHandle): RecipeOverviewViewModel =
        RecipeOverviewViewModel(recipeRepository, historyRepository, snackbarManager, combinedSearchListStore, context, handle)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        recipeRepository = mockk(relaxed = true)
        historyRepository = mockk(relaxed = true)
        combinedSearchListStore = mockk(relaxed = true)
        snackbarManager = mockk(relaxed = true)
        context = mockk(relaxed = true)

        every { combinedSearchListStore.state } returns MutableStateFlow(emptyList())
    }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `FromSearch mode populates from CombinedSearchListStore`() = testScope.runTest {
        val searchFlow = MutableStateFlow<List<FoodComponent>>(listOf(testRecipe))
        every { combinedSearchListStore.state } returns searchFlow

        val viewModel = makeViewModel(SavedStateHandle(mapOf(
            "recipeId" to testRecipe.id,
            "fromSearch" to true
        )))

        advanceUntilIdle()

        val state = viewModel.recipeOverviewState.value
        assertEquals(testRecipe.id, state.recipe.id)
        assertEquals(testRecipe.ingredients.size, state.recipe.ingredients.size)
        assertEquals(600.0, state.parameters.calories, 0.0)
        assertEquals(80.0, state.parameters.carbohydrates, 0.0)
        assertEquals(30.0, state.parameters.protein, 0.0)
        assertEquals(20.0, state.parameters.fat, 0.0)

        coVerify(exactly = 0) { recipeRepository.getRecipeById(any()) }
    }

    @Test
    fun `General mode observes recipe by id and updates state`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)

        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))

        advanceUntilIdle()

        val state = viewModel.recipeOverviewState.value
        assertEquals(testRecipe.id, state.recipe.id)
        assertEquals(testRecipe.servings, state.parameters.servings, 0.0)
        assertEquals(600.0, state.parameters.calories, 0.0)
        coVerify { recipeRepository.observeRecipeById(testRecipe.id) }
    }

    @Test
    fun `FromMeal mode loads meal item and recipe`() = testScope.runTest {
        val mealId = "m1"
        val mealItem = MealRecipeItem(
            mealId = mealId,
            recipe = testRecipe,
            quantity = 1.0,
            servings = 1.0
        )
        coEvery { historyRepository.getMealRecipeItemById(mealId, testRecipe.id) } returns mealItem

        val viewModel = makeViewModel(SavedStateHandle(mapOf(
            "recipeId" to testRecipe.id,
            "mealId" to mealId
        )))

        advanceUntilIdle()

        val state = viewModel.recipeOverviewState.value
        assertEquals(mealId, state.mealId)
        assertEquals(testRecipe.id, state.recipe.id)
        assertEquals(testRecipe.ingredients.size, state.parameters.ingredients.size)
        coVerify { historyRepository.getMealRecipeItemById(mealId, testRecipe.id) }
    }

    @Test
    fun `onServingsChanged updates servings and recalculates nutrients`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        viewModel.onEvent(RecipeOverviewEvent.ServingsChanged(3.0))
        advanceUntilIdle()

        val state = viewModel.recipeOverviewState.value
        assertEquals(3.0, state.parameters.servings, 0.0)
        assertEquals(900.0, state.parameters.calories, 0.0)
        assertEquals(120.0, state.parameters.carbohydrates, 0.0)
        assertEquals(45.0, state.parameters.protein, 0.0)
        assertEquals(30.0, state.parameters.fat, 0.0)
    }

    @Test
    fun `onDetailsClicked toggles details visibility`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        val before = viewModel.recipeOverviewState.value.parameters.showDetails
        viewModel.onEvent(RecipeOverviewEvent.ClickDetails)
        advanceUntilIdle()

        val after = viewModel.recipeOverviewState.value.parameters.showDetails
        assertEquals(!before, after)
    }

    @Test
    fun `submitRecipe returns recipe with current servings`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val viewModel = makeViewModel(
            SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        viewModel.onEvent(RecipeOverviewEvent.ServingsChanged(4.0))
        advanceUntilIdle()

        val submitted = viewModel.recipeOverviewState.value.submitRecipe()
        assertEquals(4.0, submitted.servings, 0.0)
    }

    @Test
    fun `DELETE calls recipeRepository deleteRecipe`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        viewModel.onEvent(RecipeOverviewEvent.ClickDetailsOption(DropdownMenuOptions.DELETE))
        advanceUntilIdle()

        coVerify { recipeRepository.deleteRecipe(testRecipe) }
    }

    @Test
    fun `DOWNLOAD calls recipeRepository downloadRecipe`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        viewModel.onEvent(RecipeOverviewEvent.ClickDetailsOption(DropdownMenuOptions.DOWNLOAD))
        advanceUntilIdle()

        coVerify { recipeRepository.downloadRecipe(testRecipe) }
    }

    @Test
    fun `UPLOAD success emits RecipeUploaded event`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        coEvery { recipeRepository.uploadRecipe(testRecipe) } returns Result.Success(testRecipe)
        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        val awaited = async { viewModel.events.first() }
        viewModel.onEvent(RecipeOverviewEvent.ClickDetailsOption(DropdownMenuOptions.UPLOAD))
        advanceUntilIdle()

        val event = awaited.await()
        assertTrue(event is RecipeOverviewEvent.RecipeUploaded)
    }

    @Test
    fun `UPLOAD error sets error state`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        coEvery { recipeRepository.uploadRecipe(testRecipe) } returns Result.Error(409, "Conflict")
        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        viewModel.onEvent(RecipeOverviewEvent.ClickDetailsOption(DropdownMenuOptions.UPLOAD))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is BaseViewModel.UiState.Error)
    }

    @Test
    fun `REPORT toggles report dialog visibility`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        val before = viewModel.recipeOverviewState.value.parameters.showReportDialog
        viewModel.onEvent(RecipeOverviewEvent.ClickDetailsOption(DropdownMenuOptions.REPORT))
        advanceUntilIdle()

        val after = viewModel.recipeOverviewState.value.parameters.showReportDialog
        assertEquals(!before, after)
    }

    @Test
    fun `EDIT emits NavigateToEditRecipe event`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val viewModel = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        val awaited = async { viewModel.events.first() }
        viewModel.onEvent(RecipeOverviewEvent.ClickDetailsOption(DropdownMenuOptions.EDIT))
        advanceUntilIdle()


        val event = awaited.await() as RecipeOverviewEvent.NavigateToEditRecipe
        assertEquals(testRecipe.id, event.recipeId)
    }

    @Test
    fun `FromSearch fallback loads via repository when list store has no match`() = testScope.runTest {

        every { combinedSearchListStore.state } returns MutableStateFlow(emptyList())
        coEvery { recipeRepository.getRecipeById(testRecipe.id) } returns testRecipe

        val vm = makeViewModel(SavedStateHandle(mapOf(
            "recipeId" to testRecipe.id,
            "fromSearch" to true
        )))
        advanceUntilIdle()

        val s = vm.recipeOverviewState.value
        assertEquals(testRecipe.id, s.recipe.id)
        coVerify(exactly = 1) { recipeRepository.getRecipeById(testRecipe.id) }
    }

    @Test
    fun `UpdateMealRecipeItem updates history with current servings in FromMeal mode`() = testScope.runTest {
        val mealId = "m1"
        val mealItem = MealRecipeItem(
            mealId = mealId, recipe = testRecipe, quantity = 1.0, servings = 1.0
        )
        coEvery { historyRepository.getMealRecipeItemById(mealId, testRecipe.id) } returns mealItem

        val vm = makeViewModel(SavedStateHandle(mapOf(
            "recipeId" to testRecipe.id,
            "mealId" to mealId
        )))
        advanceUntilIdle()

        vm.onEvent(RecipeOverviewEvent.ServingsChanged(2.5))
        advanceUntilIdle()

        vm.onEvent(RecipeOverviewEvent.UpdateMealRecipeItem)
        advanceUntilIdle()

        coVerify {
            historyRepository.updateMealRecipeItem(withArg { updated ->
                assertEquals(mealId, updated.mealId)
                assertEquals(testRecipe, updated.recipe)
                assertEquals(2.5, updated.servings, 0.0)
                assertEquals(2.5, updated.quantity, 0.0)
            })
        }
    }

    @Test
    fun `ResetErrorState sets UI back to Ready after an error`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        coEvery { recipeRepository.uploadRecipe(testRecipe) } returns Result.Error(409, "Conflict")

        val vm = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        vm.onEvent(RecipeOverviewEvent.ClickDetailsOption(DropdownMenuOptions.UPLOAD))
        advanceUntilIdle()
        assertTrue(vm.uiState.value is BaseViewModel.UiState.Error)

        vm.onEvent(RecipeOverviewEvent.ResetErrorState)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is BaseViewModel.UiState.Ready)
    }

    @Test
    fun `NavigateToEditRecipe event is emitted from onEvent`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val vm = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        val awaited = async { vm.events.first() }
        vm.onEvent(RecipeOverviewEvent.NavigateToEditRecipe(testRecipe.id))
        advanceUntilIdle()

        val event = awaited.await() as RecipeOverviewEvent.NavigateToEditRecipe
        assertEquals(testRecipe.id, event.recipeId)
    }

    @Test
    fun `RecipeUploaded and RecipeDeleted are no-ops but covered`() = testScope.runTest {
        coEvery { recipeRepository.observeRecipeById(testRecipe.id) } returns flowOf(testRecipe)
        val vm = makeViewModel(SavedStateHandle(mapOf("recipeId" to testRecipe.id)))
        advanceUntilIdle()

        val before = vm.uiState.value
        vm.onEvent(RecipeOverviewEvent.RecipeUploaded)
        vm.onEvent(RecipeOverviewEvent.RecipeDeleted)
        advanceUntilIdle()

        assertEquals(before, vm.uiState.value)
    }
}