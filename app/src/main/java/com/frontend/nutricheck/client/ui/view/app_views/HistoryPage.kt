package com.frontend.nutricheck.client.ui.view.app_views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.ui.view.widgets.CalorieSummary
import com.frontend.nutricheck.client.ui.view.widgets.DateSelectorBar
import com.frontend.nutricheck.client.ui.view.widgets.MealBlock
import com.frontend.nutricheck.client.ui.view_model.HistoryEvent
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.HistoryPageScreens
import java.util.Calendar
import java.util.Date

/**
 * HistoryPage is a composable function that displays the history of meals for a selected date.
 * It allows users to view their meals grouped by time of day.
 */
@Composable
fun HistoryPage(
    historyViewModel: HistoryViewModel,
    historyNavController: NavHostController
) {
    val state by historyViewModel.historyState.collectAsState()
    var selectedDate by remember { mutableStateOf(state.selectedDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val mealsGrouped = state.mealsGrouped

    val breakfastItems = mealsGrouped[DayTime.BREAKFAST] ?: emptyList()
    val breakfastCalories = breakfastItems.sumOf { meal ->
        val foodCalories = meal.mealFoodItems.sumOf { it.servings * it.foodProduct.calories * (it.servingSize.getAmount() / 100) }
        val recipeCalories = meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }
        foodCalories + recipeCalories
    }
    val breakfastComponents = breakfastItems.flatMap { it.mealFoodItems + it.mealRecipeItems }
    val lunchItems = mealsGrouped[DayTime.LUNCH] ?: emptyList()
    val lunchCalories = lunchItems.sumOf { meal ->
        val foodCalories = meal.mealFoodItems.sumOf { it.servings * it.foodProduct.calories * (it.servingSize.getAmount() / 100) }
        val recipeCalories = meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }
        foodCalories + recipeCalories
    }
    val lunchComponents = lunchItems.flatMap { it.mealFoodItems + it.mealRecipeItems }
    val dinnerItems = mealsGrouped[DayTime.DINNER] ?: emptyList()
    val dinnerCalories = dinnerItems.sumOf { meal ->
        val foodCalories = meal.mealFoodItems.sumOf { it.servings * it.foodProduct.calories * (it.servingSize.getAmount() / 100) }
        val recipeCalories = meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }
        foodCalories + recipeCalories
    }
    val dinnerComponents = dinnerItems.flatMap { it.mealFoodItems + it.mealRecipeItems }
    val snackItems = mealsGrouped[DayTime.SNACK] ?: emptyList()
    val snackCalories = snackItems.sumOf { meal ->
        val foodCalories = meal.mealFoodItems.sumOf { it.servings * it.foodProduct.calories * (it.servingSize.getAmount() / 100)}
        val recipeCalories = meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }
        foodCalories + recipeCalories
    }
    val snackComponents = snackItems.flatMap { it.mealFoodItems + it.mealRecipeItems }

    LaunchedEffect(key1 = Unit) {
        historyViewModel.events.collect { event ->
            when (event) {
                is HistoryEvent.AddEntryClick -> {
                    historyNavController.navigate(
                        HistoryPageScreens.AddMeal.createRoute(
                            dayTime = event.dayTime,
                            date = event.day.time
                        ),
                        navOptions = null
                    )
                    Log.v("HistoryPage", "Navigating to Add Meal with date: ${event.day}, dayTime: ${event.dayTime}")
                }
                is HistoryEvent.FoodClicked -> {
                    historyNavController.navigate(HistoryPageScreens.FoodDetails.createRoute(
                        mealId = event.mealId,
                        foodProductId = event.foodId
                    ))
                }
                is HistoryEvent.RecipeClicked -> {
                    historyNavController.navigate(HistoryPageScreens.RecipeDetails.createRoute(
                        mealId = event.mealId,
                        recipeId = event.recipeId
                    ))
                }
                is HistoryEvent.SelectDate -> {
                    selectedDate = event.day
                    historyViewModel.selectDate(event.day)
                }
                else -> { /* No action needed for other events */ }

            }
        }
    }

    val calendar = Calendar.getInstance()
    calendar.time = Date()

    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(colors.surface)
            .semantics { contentDescription = SemanticsTags.HISTORY_PAGE }
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        DateSelectorBar(
            selectedDate = selectedDate,
            onPreviousDay = { selectedDate = Date(selectedDate.time - 24 * 60 * 60 * 1000)
                historyViewModel.onEvent(HistoryEvent.SelectDate(selectedDate))},
            onNextDay = { selectedDate = Date(selectedDate.time + 24 * 60 * 60 * 1000)
                historyViewModel.onEvent(HistoryEvent.SelectDate(selectedDate))},
            onOpenCalendar = { showDatePicker = true }
        )
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.time)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDate = Date(it)
                                historyViewModel.onEvent(HistoryEvent.SelectDate(selectedDate))
                            }
                            showDatePicker = false
                        }
                    ) { Text(stringResource(R.string.label_ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        CalorieSummary(
            modifier = Modifier
                .padding(8.dp),
            state = state
        )
        Spacer(modifier = Modifier.height(24.dp))
        MealBlock(
            modifier = Modifier.padding(8.dp),
            stringResource(id = R.string.label_breakfast),
            breakfastCalories,
            items = breakfastComponents,
            onAddClick = { historyViewModel.onEvent(HistoryEvent.AddEntryClick(selectedDate, DayTime.BREAKFAST))},
            onItemClick = { item ->
                when (item) {
                    is MealFoodItem -> historyViewModel.onEvent(HistoryEvent.FoodClicked(item.mealId,item.foodProduct.id))
                    is MealRecipeItem -> historyViewModel.onEvent(HistoryEvent.RecipeClicked(item.mealId,item.recipe.id))
                }
            },
            onRemoveClick = { historyViewModel.onEvent(HistoryEvent.RemoveMealItem(it))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MealBlock(
            modifier = Modifier.padding(8.dp),
            stringResource(id = R.string.label_lunch),
            lunchCalories,
            items= lunchComponents,
            onAddClick = { historyViewModel.onEvent(HistoryEvent.AddEntryClick(selectedDate, DayTime.LUNCH))},
            onItemClick = { item ->
                when (item) {
                    is MealFoodItem -> historyViewModel.onEvent(HistoryEvent.FoodClicked(item.mealId,item.foodProduct.id))
                    is MealRecipeItem -> historyViewModel.onEvent(HistoryEvent.RecipeClicked(item.mealId,item.recipe.id))
                }
            },
            onRemoveClick = { historyViewModel.onEvent(HistoryEvent.RemoveMealItem(it))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MealBlock(
            modifier = Modifier.padding(8.dp),
            stringResource(id = R.string.label_dinner),
            dinnerCalories,
            items = dinnerComponents,
            onAddClick = { historyViewModel.onEvent(HistoryEvent.AddEntryClick(selectedDate, DayTime.DINNER))},
            onItemClick = { item ->
                when (item) {
                    is MealFoodItem -> historyViewModel.onEvent(HistoryEvent.FoodClicked(item.mealId,item.foodProduct.id))
                    is MealRecipeItem -> historyViewModel.onEvent(HistoryEvent.RecipeClicked(item.mealId,item.recipe.id))
                }
            },
            onRemoveClick = { historyViewModel.onEvent(HistoryEvent.RemoveMealItem(it))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MealBlock(modifier = Modifier.padding(8.dp),
            stringResource(id = R.string.label_snack),
            snackCalories,
            items = snackComponents,
            onAddClick = { historyViewModel.onEvent(HistoryEvent.AddEntryClick(selectedDate, DayTime.SNACK))},
            onItemClick = { item ->
                when (item) {
                    is MealFoodItem -> historyViewModel.onEvent(HistoryEvent.FoodClicked(item.mealId,item.foodProduct.id))
                    is MealRecipeItem -> historyViewModel.onEvent(HistoryEvent.RecipeClicked(item.mealId,item.recipe.id))
                }
            },
            onRemoveClick = { historyViewModel.onEvent(HistoryEvent.RemoveMealItem(it))
            }
        )
    }
}



