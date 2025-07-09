import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import com.frontend.nutricheck.client.ui.view.widgets.CalorieSummary
import com.frontend.nutricheck.client.ui.view.widgets.DishItemButton
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.MealBlock
import com.frontend.nutricheck.client.ui.view.widgets.MealFooter


@Composable
fun HistoryPage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    viewModel: HistoryViewModel = hiltViewModel(),
    onSwitchClick: (String) -> Unit = {}
) {
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
            historyViewModel = viewModel,
            title = "Verbleibende Kalorien",
            goalCalories = 300,
            consumedCalories = 200,
            remainingCalories = 100,
            onClick = {  }
        )
        Spacer(modifier = Modifier.height(20.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Frühstück", 300.0)
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Mittagessen", 300.0)
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Abendessen", 300.0)
        Spacer(modifier = Modifier.height(5.dp))
        MealBlock(modifier = Modifier.padding(7.dp), "Snack", 300.0)
    }
}





@Preview(showBackground = true)
@Composable
fun HistoryPagePreview() {
    val navController = rememberNavController()
    HistoryPage(actions = NavigationActions(navController)) // Dummy NavigationActions
}
