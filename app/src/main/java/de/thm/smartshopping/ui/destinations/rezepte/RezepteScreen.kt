package de.thm.smartshopping.ui.destinations.rezepte // Or your preferred package

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.thm.smartshopping.DashboardTopAppBar
import de.thm.smartshopping.methods.navBarHeight // Assuming you have this
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptCard
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptMock
import de.thm.smartshopping.ui.theme.SmartShoppingTheme

// Mock data for the screen
val mockRezepteListe = List(10) { index ->
	RezeptMock(id = (index + 1).toString(), name = "Rezept Nummer ${index + 1}")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezepteScreen(
	// For a mockup, state and events might not be strictly necessary
	// but you can add them later if needed.
	// state: RezepteScreenState, (Define this if you expand)
	// onEvent: (RezepteEvent) -> Unit, (Define this if you expand)
	navController: NavController,
) {
	Scaffold(
		modifier = Modifier.padding(bottom = navBarHeight), // If you use a custom bottom nav bar
		topBar = {
			DashboardTopAppBar( // Reusing your existing TopAppBar
				title = "Rezepte",
				showNavigationIcon = false, // Or true if you want a back button, handled by NavController
				actions = {
					IconButton(onClick = { /* TODO: Search Action */ }) {
						Icon(Icons.Filled.Search, contentDescription = "Rezepte durchsuchen")
					}
					IconButton(onClick = { /* TODO: Add Rezept Action */ }) {
						Icon(Icons.Filled.Add, contentDescription = "Neues Rezept erstellen")
					}
				}
			)
		}
	) { paddingValues ->
		if (mockRezepteListe.isEmpty()) {
			Box(
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Text(
					text = "Keine Rezepte vorhanden.",
					style = MaterialTheme.typography.bodyLarge
				)
			}
		} else {
			LazyVerticalGrid(
				columns = GridCells.Fixed(2), // Two items per row
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize(),
				contentPadding = PaddingValues(8.dp), // Overall padding for the grid
				verticalArrangement = Arrangement.spacedBy(2.dp), // Space between rows
				horizontalArrangement = Arrangement.spacedBy(2.dp) // Space between columns
			) {
				items(
					items = mockRezepteListe,
					key = { it.id }
				) { rezept ->
					RezeptCard(
						rezept = rezept,
						onClick = {
							// TODO: Navigate to rezept details screen
							// navController.navigate("rezept_details/${rezept.id}")
							println("Clicked on ${rezept.name}")
						}
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun RezepteScreenPreview() {
	SmartShoppingTheme {
		RezepteScreen(
			navController = rememberNavController()
		)
	}
}
