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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.widgets.CalorieSummary
import com.frontend.nutricheck.client.ui.view.widgets.MealBlock
import com.frontend.nutricheck.client.ui.view_model.HistoryEvent
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.DiaryScreens


@Composable
fun HistoryPage(
    historyViewModel: HistoryViewModel,
    onSwitchClick: (String) -> Unit = {}
) {

    LaunchedEffect(key1 = Unit) {
        historyViewModel.events.collect { event ->
            when (event) {
                is HistoryEvent.DisplayMealsOfDay -> {
                    // Handle displaying meals of the day
                }
                is HistoryEvent.DisplayNutritionOfDay ->  {
                    // Handle displaying nutrition of the day
                }

                else -> { /* No action needed for other events */ }

            }
        }
    }


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.Black)
    ) {
        CalorieSummary(
            modifier = Modifier
                .padding(7.dp),
            historyViewModel = historyViewModel,
            title = "Verbleibende Kalorien",
            goalCalories = 300,
            consumedCalories = 200,
            remainingCalories = 100,
            onClick = {  }
        )
        Spacer(modifier = Modifier.height(20.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Frühstück", 300.0, meals= emptyList())
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Mittagessen", 300.0, meals= emptyList())
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Abendessen", 300.0, meals= emptyList())
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Snack", 300.0, meals= emptyList())
    }
}





@Preview(showBackground = true)
@Composable
fun HistoryPagePreview() {
    HistoryPage(
        historyViewModel = hiltViewModel(),
        onSwitchClick = {}
    )
}
