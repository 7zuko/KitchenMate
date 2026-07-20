package de.thm.smartshopping.ui.destinations.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import de.thm.smartshopping.DashboardTopAppBar
import de.thm.smartshopping.ui.destinations.dashboard.viewmodels.DashboardViewModel
import de.thm.smartshopping.NavDestination
import de.thm.smartshopping.R
import de.thm.smartshopping.data.MealPlan
import de.thm.smartshopping.data.VorratsArtikel
import de.thm.smartshopping.ui.destinations.dashboard.composables.DashboardCard
import de.thm.smartshopping.ui.destinations.speiseplan.models.MealType
import de.thm.smartshopping.utils.resttageText
import de.thm.smartshopping.utils.tageBisAblauf
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.isNotEmpty
import kotlin.collections.take

data class DashboardItem(
    val title: String,
    val image: Int,
    val route: String,
    val color: Color
)
@RequiresApi(Build.VERSION_CODES.O)
private fun greeting(): String {

    val hour = LocalTime.now().hour

    return when (hour) {
        in 5..11 -> "Guten Morgen ☀️"
        in 12..17 -> "Guten Tag 👋"
        in 18..22 -> "Guten Abend 🌙"
        else -> "Hallo 👋"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    navController: NavController
) {

    val viewModel: DashboardViewModel = hiltViewModel()

    val todayMealPlans by viewModel.todayMealPlans.collectAsState()

    val baldAblaufendeArtikel by
    viewModel.baldAblaufendeArtikel.collectAsState()

    val abgelaufeneArtikel by
    viewModel.abgelaufeneArtikel.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                title = {
                    Text("KitchenMate")
                },
                showNavigationIcon = false
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            val currentDate = DateTimeFormatter
                .ofPattern("EEEE, dd. MMMM", Locale.GERMAN)
                .format(java.time.LocalDate.now())

            item {

                Text(
                    text = greeting(),
                    style = MaterialTheme.typography.headlineMedium
                )

            }

            item {

                Text(
                    text = currentDate.replaceFirstChar {
                        it.uppercase()
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

            item {
                HeuteCard(
                    mealPlans = todayMealPlans
                )
            }

            if (
                baldAblaufendeArtikel.isNotEmpty() ||
                abgelaufeneArtikel.isNotEmpty()
            ) {

                item {
                    MhdWarnCard(

                        abgelaufeneArtikel = abgelaufeneArtikel,

                        baldAblaufendeArtikel = baldAblaufendeArtikel,

                        onClick = {

                            navController.navigate(
                                NavDestination.Verwaltung.route
                            )

                        }

                    )
                }

            }

            val dashboardItems = listOf(

                DashboardItem(
                    title = "Artikel",
                    image = R.drawable.dashboard_artikel,
                    route = NavDestination.Verwaltung.route,
                    color = Color(0xFFF1FAEE)
                ),

                DashboardItem(
                    title = "Rezepte",
                    image = R.drawable.dashboard_rezepte,
                    route = NavDestination.Rezepte.route,
                    color = Color(0xFFFFF4E5)
                ),

                DashboardItem(
                    title = "Speiseplan",
                    image = R.drawable.dashboard_speiseplan,
                    route = NavDestination.Speiseplan.route,
                    color = Color(0xFFF6F0FF)
                ),

                DashboardItem(
                    title = "Einkaufslisten",
                    image = R.drawable.dashboard_einkaufslisten,
                    route = NavDestination.Einkaufslisten.route,
                    color = Color(0xFFEFF8FF)
                )

            )

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Artikel",
                        image = R.drawable.dashboard_artikel,
                        containerColor = Color(0xFFF1FAEE)
                    ) {
                        navController.navigate(NavDestination.Verwaltung.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Rezepte",
                        image = R.drawable.dashboard_rezepte,
                        containerColor = Color(0xFFFFF4E5)
                    ) {
                        navController.navigate(NavDestination.Rezepte.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Speiseplan",
                        image = R.drawable.dashboard_speiseplan,
                        containerColor = Color(0xFFF6F0FF)
                    ) {
                        navController.navigate(NavDestination.Speiseplan.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Einkaufslisten",
                        image = R.drawable.dashboard_einkaufslisten,
                        containerColor = Color(0xFFEFF8FF)
                    ) {
                        navController.navigate(NavDestination.Einkaufslisten.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HeuteCard(
    mealPlans: List<MealPlan>
) {
    val currentMealType = when (LocalTime.now().hour) {
        in 2..9 -> MealType.BREAKFAST
        in 10..17 -> MealType.LUNCH
        else -> MealType.DINNER
    }

    val currentMeal = mealPlans.find {
        it.mealType == currentMealType
    }

    val breakfast =
        mealPlans.find {
            it.mealType == MealType.BREAKFAST
        }

    val lunch =
        mealPlans.find {
            it.mealType == MealType.LUNCH
        }

    val dinner =
        mealPlans.find {
            it.mealType == MealType.DINNER
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F5F0)
        ),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    text = when (currentMealType) {
                        MealType.BREAKFAST -> "🥣 Frühstück"
                        MealType.LUNCH -> "🍝 Mittagessen"
                        MealType.DINNER -> "🌙 Abendessen"
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentMeal?.rezept?.name ?: "Noch nichts geplant",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Passe deinen Speiseplan jederzeit an.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MhdWarnCard(
    abgelaufeneArtikel: List<VorratsArtikel>,
    baldAblaufendeArtikel: List<VorratsArtikel>,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E8)
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "⚠️ Haltbarkeits-Warnungen",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(12.dp))

            if (abgelaufeneArtikel.isNotEmpty()) {



                Spacer(Modifier.height(6.dp))

                abgelaufeneArtikel
                    .take(2)
                    .forEach {

                        MhdArtikelZeile(
                            emoji = it.artikel.emoji,
                            name = it.artikel.name,
                            status = "Abgelaufen",
                            statusColor = MaterialTheme.colorScheme.error
                        )

                    }

                Spacer(Modifier.height(6.dp))

            }

            if (baldAblaufendeArtikel.isNotEmpty()) {

                Spacer(Modifier.height(6.dp))

                baldAblaufendeArtikel
                    .take(3)
                    .forEach {

                        val resttage =
                            tageBisAblauf(
                                it.mindesthaltbarBis!!
                            )

                        MhdArtikelZeile(
                            emoji = it.artikel.emoji,
                            name = it.artikel.name,
                            status = resttageText(it.mindesthaltbarBis!!),
                            statusColor = Color(0xFFE69F00)
                        )
                    }

            }
        }
    }
}

@Composable
private fun MhdArtikelZeile(
    emoji: String,
    name: String,
    status: String,
    statusColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = emoji,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        Surface(
            shape = RoundedCornerShape(50),
            color = statusColor.copy(alpha = 0.15f)
        ) {

            Text(
                text = status,
                color = statusColor,
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                ),
                style = MaterialTheme.typography.labelMedium
            )

        }

    }

}