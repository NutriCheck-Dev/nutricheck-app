package com.nutricheck.frontend.viewmodels.recipe

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecipePageViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private lateinit var repository: RecipeRepository

    private val recipe1 = Recipe(id = "r1", name = "Apple Pie", calories = 300.0, carbohydrates = 40.0, protein = 10.0, fat = 10.0, servings = 2)
    private val recipe2= Recipe(id = "r2", name = "Pie Apple", calories = 250.0, carbohydrates = 35.0, protein = 8.0, fat = 9.0, servings = 1)
    private val recipe3 = Recipe(id = "r3", name = "Pineapple Tart", calories = 200.0, carbohydrates = 30.0, protein = 5.0, fat = 7.0, servings = 1)

    private fun makeViewModel() = RecipePageViewModel(repository)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk(relaxed = true)

        coEvery { repository.observeMyRecipes() } returns flowOf(listOf(recipe1, recipe2, recipe3))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads myRecipes and sets default state`() = testScope.runTest {
        val viewModel = makeViewModel()
        testScheduler.advanceTimeBy(200)
        advanceUntilIdle()

        val state = viewModel.recipePageState.value
        assertEquals(listOf("r1", "r2", "r3"), state.myRecipes.map { it.id })
        assertEquals(0, state.selectedTab)
        assertEquals("", state.query)
        assertTrue(state.onlineRecipes.isEmpty())
    }

    @Test
    fun `QueryChanged filters and sorts by rank then position then name`() = testScope.runTest {
        coEvery { repository.observeMyRecipes() } returns flowOf(listOf(recipe1, recipe2, recipe3))
        val viewModel = makeViewModel()
        testScheduler.advanceTimeBy(200)
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.QueryChanged("apple"))
        testScheduler.advanceTimeBy(200)
        advanceUntilIdle()

        val state = viewModel.recipePageState.value
        assertEquals(listOf("r1", "r2", "r3"), state.myRecipes.map { it.id  })
    }

    @Test
    fun `QueryChanged to no-match results in empty myRecipes`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.QueryChanged("zzz"))
        testScheduler.advanceTimeBy(200)
        advanceUntilIdle()

        assertTrue(viewModel.recipePageState.value.myRecipes.isEmpty())
    }

    @Test
    fun `ClickMyRecipes sets tab`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.ClickMyRecipes)
        advanceUntilIdle()

        assertEquals(0, viewModel.recipePageState.value.selectedTab)
    }

    @Test
    fun `ClickOnlineRecipes sets tab`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.ClickOnlineRecipes)
        advanceUntilIdle()

        assertEquals(1, viewModel.recipePageState.value.selectedTab)
    }

    @Test
    fun `ShowDetailsMenu sets expandedRecipeId and can collapse`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.ShowDetailsMenu("r2"))
        advanceUntilIdle()
        assertEquals("r2", viewModel.recipePageState.value.expandedRecipeId)

        viewModel.onEvent(RecipePageEvent.ShowDetailsMenu(null))
        advanceUntilIdle()
        assertNull(viewModel.recipePageState.value.expandedRecipeId)
    }

    @Test
    fun `SearchOnline with blank query clears list and stays ready`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.QueryChanged("  "))
        testScheduler.advanceTimeBy(200)
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.SearchOnline)
        advanceUntilIdle()

        val state = viewModel.recipePageState.value
        assertTrue(state.onlineRecipes.isEmpty())
    }

    @Test
    fun `SearchOnline success populates list and sets flags`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.QueryChanged("pie"))
        testScheduler.advanceTimeBy(200)
        advanceUntilIdle()

        coEvery { repository.searchRecipes("pie") } returns flowOf(Result.Success(listOf(recipe1, recipe2)))

        viewModel.onEvent(RecipePageEvent.SearchOnline)
        advanceUntilIdle()

        val state = viewModel.recipePageState.value
        assertEquals(listOf("r1", "r2"), state.onlineRecipes.map { it.id })
        assertTrue(state.hasSearched)
        assertEquals("pie", state.lastSearchedQuery)
    }

    @Test
    fun `SearchOnline error result empties list and sets error`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.QueryChanged("pie"))
        testScheduler.advanceTimeBy(200)
        advanceUntilIdle()

        coEvery { repository.searchRecipes("pie") } returns flowOf(Result.Error(400, "nope"))

        viewModel.onEvent(RecipePageEvent.SearchOnline)
        advanceUntilIdle()

        val state = viewModel.recipePageState.value
        assertTrue(state.onlineRecipes.isEmpty())
    }

    @Test
    fun `SearchOnline exception is caught, empties list and sets error`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.QueryChanged("pie"))
        testScheduler.advanceTimeBy(200)
        advanceUntilIdle()

        coEvery { repository.searchRecipes("pie") } returns flow {
            throw RuntimeException("boom")
        }

        viewModel.onEvent(RecipePageEvent.SearchOnline)
        advanceUntilIdle()

        assertTrue(viewModel.recipePageState.value.onlineRecipes.isEmpty())
    }

    @Test
    fun `Details DELETE calls deleteRecipe`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        viewModel.onEvent(RecipePageEvent.ClickDetailsOption(recipe1, DropdownMenuOptions.DELETE))
        advanceUntilIdle()

        coVerify { repository.deleteRecipe(recipe1) }
    }

    @Test
    fun `Details UPLOAD success emits RecipeUploaded`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        coEvery { repository.uploadRecipe(any()) } returns Result.Success(recipe3)

        val awaited = async { viewModel.events.first() }
        viewModel.onEvent(RecipePageEvent.ClickDetailsOption(recipe3, DropdownMenuOptions.UPLOAD))
        advanceUntilIdle()

        assertTrue(awaited.await() is RecipePageEvent.RecipeUploaded)
    }

    @Test
    fun `Details UPLOAD error sets error state`() = testScope.runTest {
        coEvery { repository.observeMyRecipes() } returns flowOf(emptyList())
        val viewModel = makeViewModel()
        advanceUntilIdle()

        coEvery { repository.uploadRecipe(any()) } returns Result.Error(409, "conflict")

        val awaited = async { viewModel.uiState.drop(1).first() }
        viewModel.onEvent(RecipePageEvent.ClickDetailsOption(recipe3, DropdownMenuOptions.UPLOAD))
        val newState = awaited.await()
        assertTrue(newState is BaseViewModel.UiState.Error)
    }

    @Test
    fun `Details REPORT toggles dialog`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        val before = viewModel.recipePageState.value.showReportDialog
        viewModel.onEvent(RecipePageEvent.ClickDetailsOption(recipe1, DropdownMenuOptions.REPORT))
        advanceUntilIdle()
        val after = viewModel.recipePageState.value.showReportDialog

        assertEquals(!before, after)
    }

    @Test
    fun `Details EDIT emits NavigateToEditRecipe`() = testScope.runTest {
        val viewModel = makeViewModel()
        advanceUntilIdle()

        val awaited = async { viewModel.events.first() }
        viewModel.onEvent(RecipePageEvent.ClickDetailsOption(recipe1, DropdownMenuOptions.EDIT))
        advanceUntilIdle()

        val event = awaited.await() as RecipePageEvent.NavigateToEditRecipe
        assertEquals("r1", event.recipeId)
    }

}