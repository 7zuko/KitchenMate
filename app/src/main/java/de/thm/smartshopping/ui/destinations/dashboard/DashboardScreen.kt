package de.thm.smartshopping.ui.destinations.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.thm.smartshopping.DashboardTopAppBar
import de.thm.smartshopping.NavDestination
import de.thm.smartshopping.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val currentDate = DateTimeFormatter
                .ofPattern("EEEE, dd. MMMM", Locale.GERMAN)
                .format(java.time.LocalDate.now())

            Text(
                text = greeting(),
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = currentDate.replaceFirstChar {
                    it.uppercase()
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Was möchtest du heute machen?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            HeuteCard()

            Spacer(modifier = Modifier.height(18.dp))

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

            LazyVerticalGrid(

                columns = GridCells.Fixed(2),

                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                horizontalArrangement = Arrangement.spacedBy(16.dp),

                verticalArrangement = Arrangement.spacedBy(16.dp),

                contentPadding = PaddingValues(vertical = 12.dp)

            ) {

                items(dashboardItems) { item ->

                    DashboardCard(
                        title = item.title,
                        image = item.image,
                        containerColor = item.color
                    ) {

                        if (item.route == "speiseplan") {

                            // kommt später

                        } else {

                            navController.navigate(item.route)

                        }

                    }

                }

            }

            Text(
                text = "🍽 Heutiges Gericht",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Noch kein Gericht geplant."
            )
        }
    }
}

@Composable
private fun HeuteCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
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
                    text = "Heute",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Noch kein Gericht geplant.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Plane deine erste Woche im Speiseplan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}