import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.model.data_sources.data.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.HistoryDay
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.ui.view.widgets.CalorieSummary
import com.frontend.nutricheck.client.ui.view.widgets.DateSelectorBar
import com.frontend.nutricheck.client.ui.view.widgets.MealBlock
import com.frontend.nutricheck.client.ui.view_model.history.HistoryEvent
import com.frontend.nutricheck.client.ui.view_model.history.HistoryViewModel
import java.util.Calendar
import java.util.Date


@Composable
fun HistoryPage(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    onSwitchClick: (String) -> Unit = {}
) {
    val state by historyViewModel.historyState.collectAsState()
    val scrollState = rememberScrollState()

    val breakfast = state.mealsGrouped[DayTime.BREAKFAST].orEmpty()
    val breakfastItems = breakfast.flatMap { it.items }.toList()
    val lunch = state.mealsGrouped[DayTime.LUNCH].orEmpty()
    val lunchItems = lunch.flatMap { it.items }.toList()

    val dinner = state.mealsGrouped[DayTime.DINNER].orEmpty()
    val dinnerItems = dinner.flatMap { it.items }.toList()
    val snack = state.mealsGrouped[DayTime.SNACK].orEmpty()
    val snackItems = snack.flatMap { it.items }.toList()



    LaunchedEffect(key1 = Unit) {
        historyViewModel.events.collect { event ->
            when (event) {
                is HistoryEvent.AddEntryClick -> {
                    // Handle displaying meals of the day
                }


                else -> { /* No action needed for other events */ }

            }
        }
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.time = Date()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.Black)
    ) {
        Spacer(modifier = Modifier.height(7.dp))
        DateSelectorBar(
            selectedDate = calendar.time,
            onPreviousDay = { /* Handle previous day */ },
            onNextDay = { /* Handle next day */ },
            onOpenCalendar = {
                DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        // Handle date selection
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        )

        CalorieSummary(
            modifier = Modifier
                .padding(7.dp),
            title = "Verbleibende Kalorien",
            goalCalories = 300,
            consumedCalories = 200,
            remainingCalories = 100,

        )
        Spacer(modifier = Modifier.height(20.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Frühstück", 300.0, meals= breakfastItems, onAddClick = { historyViewModel.onAddEntryClick(calendar.time) })
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Mittagessen", 300.0, meals= lunchItems, onAddClick = { historyViewModel.onAddEntryClick(calendar.time) })
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Abendessen", 300.0, meals= dinnerItems, onAddClick = { historyViewModel.onAddEntryClick(calendar.time) })
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Snack", 300.0, meals= snackItems, onAddClick = { historyViewModel.onAddEntryClick(calendar.time) })
    }
}

// Fake-Repository für die Preview
class FakeHistoryRepository : HistoryRepository {
    // Implementiere alle Methoden mit Dummy-Daten oder leeren Listen
    override suspend fun getCalorieHistory() = emptyList<HistoryDay>()
    override suspend fun getDailyHistory(date: Date) = HistoryDay()
    override suspend fun requestAiMeal() = Meal()
    override suspend fun deleteMeal(meal: Meal) {}
    override suspend fun updateMeal(meal: Meal) {}
    override suspend fun getMealsForDay(date: Date) = emptyList<Meal>()
    override suspend fun addFoodToMeal(name: String, foodId: String) {}
    override suspend fun removeFoodFromMeal(name: String, foodId: String) {}
    override suspend fun getHistoryByDate(date: Date) = kotlinx.coroutines.flow.flow { emit(HistoryDay()) }
    override suspend fun addMeal(meal: Meal) {}
    override suspend fun saveAsRecipe(meal: Meal, recipeName: String, recipeDescription: String) {}
}



// FakeViewModel mit Repository-Dependency
class FakeHistoryViewModel : HistoryViewModel(FakeHistoryRepository())

@Preview(showBackground = true)
@Composable
fun HistoryPagePreview() {
    val fakeViewModel = remember { FakeHistoryViewModel() }
    HistoryPage(historyViewModel = fakeViewModel)
}
