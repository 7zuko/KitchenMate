package de.thm.smartshopping.ui.destinations.speiseplan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.thm.smartshopping.DashboardTopAppBar
import de.thm.smartshopping.ui.destinations.speiseplan.components.DayPlanCard
import de.thm.smartshopping.ui.destinations.speiseplan.components.RecipePickerBottomSheet
import de.thm.smartshopping.ui.destinations.speiseplan.components.WeekSelector
import de.thm.smartshopping.ui.destinations.speiseplan.components.WeekShoppingListCard
import de.thm.smartshopping.ui.destinations.speiseplan.events.SpeiseplanEvent
import de.thm.smartshopping.ui.destinations.speiseplan.viewmodels.SpeiseplanViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val days = listOf(
    "Montag",
    "Dienstag",
    "Mittwoch",
    "Donnerstag",
    "Freitag",
    "Samstag",
    "Sonntag"
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SpeiseplanScreen() {

    val viewModel: SpeiseplanViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    val today = LocalDate.now()

    val monday = today.with(DayOfWeek.MONDAY)

    val selectedDate = monday.plusDays(state.selectedDay.toLong())

    val formatter = DateTimeFormatter.ofPattern(
        "dd. MMMM yyyy",
        Locale.GERMAN
    )

    val formattedDate = selectedDate.format(formatter)

    val isToday = selectedDate == today

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                title = {
                    Text("Speiseplan")
                },
                showNavigationIcon = false
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(
                top = 20.dp,
                bottom = 120.dp
            )
        ) {

            item {

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Plane deine Woche",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "Wähle einen Tag und plane deine Mahlzeiten.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }

            }

            item {

                WeekSelector(
                    selectedDay = state.selectedDay,
                    onDaySelected = {
                        viewModel.onEvent(
                            SpeiseplanEvent.SelectDay(it)
                        )
                    }
                )

            }


            item {

                DayPlanCard(
                    day = days[state.selectedDay],
                    date = formattedDate,
                    isToday = isToday,
                    mealPlans = state.mealPlans,
                    selectedDay = state.selectedDay,
                    onAddMeal = { meal ->

                        viewModel.onEvent(
                            SpeiseplanEvent.ShowRecipeSheet(
                                show = true,
                                meal = meal
                            )
                        )
                    },
                    onCreateShoppingList = {

                        viewModel.onEvent(

                            SpeiseplanEvent.CreateShoppingListFromDay

                        )

                    }
                )

            }

            item {

                WeekShoppingListCard(
                    onCreateShoppingList = {
                        viewModel.onEvent(
                            SpeiseplanEvent.CreateShoppingListFromWeek
                        )
                    },
                )

            }
        }

        if (state.showRecipeSheet) {

            RecipePickerBottomSheet(
                recipes = state.rezepte,

                onRecipeSelected = { rezept ->

                    viewModel.onEvent(
                        SpeiseplanEvent.SelectRecipe(rezept)
                    )
                                   },

                onDismiss = {

                    viewModel.onEvent(
                        SpeiseplanEvent.ShowRecipeSheet(false)
                    )
                }
            )
        }
    }
}