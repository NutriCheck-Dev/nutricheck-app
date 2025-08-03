package com.frontend.nutricheck.client.ui.view.app_views

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
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view.widgets.CalorieSummary
import com.frontend.nutricheck.client.ui.view.widgets.DateSelectorBar
import com.frontend.nutricheck.client.ui.view.widgets.MealBlock
import com.frontend.nutricheck.client.ui.view_model.HistoryEvent
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.AddDialogOrigin
import com.frontend.nutricheck.client.ui.view_model.navigation.Screen
import java.util.Calendar
import java.util.Date


@Composable
fun HistoryPage(
    historyViewModel: HistoryViewModel,
    historyPageNavController: NavHostController,
    mainNavController: NavHostController
) {
    val state by historyViewModel.historyState.collectAsState()
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val mealsGrouped = state.mealsGrouped

    val breakfastItems = mealsGrouped[DayTime.BREAKFAST] ?: emptyList()
    val breakfastCalories = breakfastItems.sumOf { meal ->
        val foodCalories = meal.mealFoodItems.sumOf { it.quantity * it.foodProduct.calories }
        val recipeCalories = meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }
        foodCalories + recipeCalories
    }
    val breakfastComponents = breakfastItems.flatMap { it.mealFoodItems + it.mealRecipeItems }
    val lunchItems = mealsGrouped[DayTime.LUNCH] ?: emptyList()
    val lunchCalories = lunchItems.sumOf { meal ->
        val foodCalories = meal.mealFoodItems.sumOf { it.quantity * it.foodProduct.calories }
        val recipeCalories = meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }
        foodCalories + recipeCalories
    }
    val lunchComponents = lunchItems.flatMap { it.mealFoodItems + it.mealRecipeItems }
    val dinnerItems = mealsGrouped[DayTime.DINNER] ?: emptyList()
    val dinnerCalories = dinnerItems.sumOf { meal ->
        val foodCalories = meal.mealFoodItems.sumOf { it.quantity * it.foodProduct.calories }
        val recipeCalories = meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }
        foodCalories + recipeCalories
    }
    val dinnerComponents = dinnerItems.flatMap { it.mealFoodItems + it.mealRecipeItems }
    val snackItems = mealsGrouped[DayTime.SNACK] ?: emptyList()
    val snackCalories = snackItems.sumOf { meal ->
        val foodCalories = meal.mealFoodItems.sumOf { it.quantity * it.foodProduct.calories }
        val recipeCalories = meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }
        foodCalories + recipeCalories
    }
    val snackComponents = snackItems.flatMap { it.mealFoodItems + it.mealRecipeItems }

    LaunchedEffect(key1 = Unit) {
        historyViewModel.events.collect { event ->
            when (event) {
                is HistoryEvent.AddEntryClick -> {
                    mainNavController.navigate(Screen.Add.createRoute(AddDialogOrigin.HISTORY_PAGE, event.day, event.dayTime)) {
                        launchSingleTop = true
                    }
                }
                is HistoryEvent.FoodClicked -> {
                    historyPageNavController.navigate(
                        "food_details?mealId=${event.mealId}&foodProductId=${event.foodId}"
                    )
                }
                is HistoryEvent.RecipeClicked -> {
                    historyPageNavController.navigate(
                        "recipe_details?recipeId=${event.recipeId}&mealId=${event.mealId}"
                    )
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
    ) {
        Spacer(modifier = Modifier.height(7.dp))
        DateSelectorBar(
            selectedDate = selectedDate,
            onPreviousDay = { selectedDate = Date(selectedDate.time - 24 * 60 * 60 * 1000) },
            onNextDay = { selectedDate = Date(selectedDate.time + 24 * 60 * 60 * 1000) },
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
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Abbrechen") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        CalorieSummary(
            modifier = Modifier
                .padding(7.dp),
            state = state
        )
        Spacer(modifier = Modifier.height(20.dp))
        MealBlock(
            modifier = Modifier.padding(7.dp),
            stringResource(id = R.string.label_breakfast),
            breakfastCalories,
            items = breakfastComponents,
            onAddClick = { historyViewModel.onEvent(HistoryEvent.AddEntryClick(selectedDate, DayTime.BREAKFAST))},
            onItemClick = { item ->
                when (item) {
                    is MealFoodItem -> historyViewModel.onEvent(HistoryEvent.FoodClicked(item.mealId,item.foodProduct.id))
                    is MealRecipeItem -> historyViewModel.onEvent(HistoryEvent.RecipeClicked(item.mealId,item.recipe.id))
                }
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(
            modifier = Modifier.padding(7.dp),
            stringResource(id = R.string.label_lunch),
            lunchCalories,
            items= lunchComponents,
            onAddClick = { historyViewModel.onEvent(HistoryEvent.AddEntryClick(selectedDate, DayTime.LUNCH))},
            onItemClick = { item ->
                when (item) {
                    is MealFoodItem -> historyViewModel.onEvent(HistoryEvent.FoodClicked(item.mealId,item.foodProduct.id))
                    is MealRecipeItem -> historyViewModel.onEvent(HistoryEvent.RecipeClicked(item.mealId,item.recipe.id))
                }
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(
            modifier = Modifier.padding(7.dp),
            stringResource(id = R.string.label_dinner),
            dinnerCalories,
            items = dinnerComponents,
            onAddClick = { historyViewModel.onEvent(HistoryEvent.AddEntryClick(selectedDate, DayTime.DINNER))},
            onItemClick = { item ->
                when (item) {
                    is MealFoodItem -> historyViewModel.onEvent(HistoryEvent.FoodClicked(item.mealId,item.foodProduct.id))
                    is MealRecipeItem -> historyViewModel.onEvent(HistoryEvent.RecipeClicked(item.mealId,item.recipe.id))
                }
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp),
            stringResource(id = R.string.label_snack),
            snackCalories,
            items = snackComponents,
            onAddClick = { historyViewModel.onEvent(HistoryEvent.AddEntryClick(selectedDate, DayTime.SNACK))},
            onItemClick = { item ->
                when (item) {
                    is MealFoodItem -> historyViewModel.onEvent(HistoryEvent.FoodClicked(item.mealId,item.foodProduct.id))
                    is MealRecipeItem -> historyViewModel.onEvent(HistoryEvent.RecipeClicked(item.mealId,item.recipe.id))
                }
            }
        )
    }
}



