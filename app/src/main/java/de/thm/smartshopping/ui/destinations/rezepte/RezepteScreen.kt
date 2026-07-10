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
import de.thm.smartshopping.methods.navBarHeight
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptCard
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.data.RezeptZutat
import de.thm.smartshopping.ui.composables.AddArtikelSheet
import de.thm.smartshopping.ui.composables.SearchTopBar
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptCreateSheet
import de.thm.smartshopping.ui.destinations.rezepte.events.RezepteEvent
import de.thm.smartshopping.ui.destinations.rezepte.states.RezepteScreenState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezepteScreen(
	state: RezepteScreenState,
	onEvent: (RezepteEvent) -> Unit,
	navController: NavController
) {

	var isSearching by remember {
		mutableStateOf(false)
	}

	var searchText by remember {
		mutableStateOf("")
	}

	var selectedArtikel by remember {
		mutableStateOf<Artikel?>(null)
	}

	var pendingArtikelId by remember {
		mutableStateOf<String?>(null)
	}

	var zutaten by remember {
		mutableStateOf<List<RezeptZutat>>(emptyList())
	}

	var showAddZutatSheet by remember {
		mutableStateOf(false)
	}

	val scope = rememberCoroutineScope()

	val createSheetState =
		rememberModalBottomSheetState(
			skipPartiallyExpanded = true
		)

	val artikelSheetState =
		rememberModalBottomSheetState(
			skipPartiallyExpanded = true
		)

	LaunchedEffect(state.showCreateSheet) {
		scope.launch {

			if (state.showCreateSheet) {
				createSheetState.show()
			} else {

				if (createSheetState.isVisible) {
					createSheetState.hide()
				}
			}
		}
	}

	LaunchedEffect(state.showArtikelSheet) {

		scope.launch {

			if (state.showArtikelSheet) {

				artikelSheetState.show()

			} else {

				if (artikelSheetState.isVisible) {

					artikelSheetState.hide()

				}
			}
		}
	}

	Scaffold(
		floatingActionButton = {
			FloatingActionButton(
				onClick = {
					onEvent(
						RezepteEvent.ShowCreateSheet(true)
					)
				}
			) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = "Rezept erstellen"
				)
			}
		},
		modifier = Modifier.padding(bottom = navBarHeight), // If you use a custom bottom nav bar
		topBar = {
			DashboardTopAppBar(
				title = {
					if (!isSearching) {
						Text("Rezepte")
					}
				},
				showNavigationIcon = false,
				actions = {

					if (isSearching) {

						SearchTopBar(
							searchText = searchText,
							placeholder = "Rezepte suchen",

							onSearchTextChange = {
								searchText = it
							},

							onClose = {
								searchText = ""
								isSearching = false
							}
						)

					} else {

						IconButton(
							onClick = {
								isSearching = true
							}
						) {
							Icon(
								imageVector = Icons.Default.Search,
								contentDescription = "Suche"
							)
						}
					}
				}
			)
		}
	) { paddingValues ->
		val gefilterteRezepte =
			state.rezepte.filter {
				searchText.isBlank() ||
						it.name.contains(
							searchText,
							ignoreCase = true
						)
			}

		if (state.rezepte.isEmpty()) {
			Box(
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally
				) {

					Text(
						text = "🍳",
						style = MaterialTheme.typography.headlineLarge
					)

					Text(
						text = "Noch keine Rezepte",
						style = MaterialTheme.typography.headlineSmall
					)

					Text(
						text = "Erstelle dein erstes Rezept",
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}
		} else if (gefilterteRezepte.isEmpty()) {
			Box(

				modifier = Modifier.fillMaxSize(),

				contentAlignment = Alignment.Center

			) {

				Column(

					horizontalAlignment = Alignment.CenterHorizontally

				) {

					Text(

						text = "🔍",

						style = MaterialTheme.typography.headlineLarge

					)

					Text(

						text = "Keine Rezepte gefunden",

						style = MaterialTheme.typography.headlineSmall

					)

					Text(

						text = "Versuche einen anderen Suchbegriff",

						style = MaterialTheme.typography.bodyMedium

					)

				}

			}
		} else {
			LazyVerticalGrid(
				columns = GridCells.Fixed(2), // Two items per row
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize(),
				contentPadding = PaddingValues(16.dp), // Overall padding for the grid
				verticalArrangement = Arrangement.spacedBy(12.dp), // Space between rows
				horizontalArrangement = Arrangement.spacedBy(12.dp) // Space between columns
			) {
				items(
					items = gefilterteRezepte,
					key = { it.id }
				) { rezept ->
					RezeptCard(
						rezept = rezept,
						onClick = {
							println("Navigiere zu Rezept: ${rezept.id}")
							navController.navigate(
								"rezept_details/${rezept.id}"
							)
						}
					)
				}
			}
		}
	}

	if (state.showCreateSheet) {

		RezeptCreateSheet(
			sheetState = createSheetState,

			onDismiss = {
				onEvent(
					RezepteEvent.ShowCreateSheet(false)
				)

				zutaten = emptyList()

				selectedArtikel = null

				pendingArtikelId = null

				showAddZutatSheet = false
			},

			onCreateConfirmed = {
					name,
					beschreibung,
					zeit,
					bildPfad,
					kategorie,
					schwierigkeit,
					rezeptZutaten ->

				onEvent(
					RezepteEvent.CreateRezept(
						Rezept(
							id = System.currentTimeMillis().toString(),
							name = name,
							beschreibung = beschreibung,
							zubereitungszeit = zeit,
							bildPfad = bildPfad,
							kategorie = kategorie,
							schwierigkeit = schwierigkeit,
							zutaten = rezeptZutaten
						)
					)
				)

				zutaten = emptyList()
				selectedArtikel = null
				pendingArtikelId = null
				showAddZutatSheet = false
			},

			allArtikel = state.allArtikel,

			zutaten = zutaten,

			onZutatenChanged = {
				zutaten = it
			},

			onAddZutatClicked = {
				showAddZutatSheet = true
			},

			onAddArtikelClicked = {
				onEvent(
					RezepteEvent.ShowArtikelSheet(true)
				)
			},

			selectedArtikel = selectedArtikel,

			onArtikelSelected = {
				selectedArtikel = it
			},

			showAddZutatSheet = showAddZutatSheet,

			onShowAddZutatSheet = {
				showAddZutatSheet = it
			},

			pendingArtikelId = pendingArtikelId,

			onPendingArtikelIdChanged = {
				pendingArtikelId = it
			},
		)
	}

	if (state.showArtikelSheet) {

		AddArtikelSheet(

			sheetState = artikelSheetState,

			currentArtikel = null,

			allArtikel = state.allArtikel,

			allKategorien = state.allKategorien,

			onSaveArtikel = { artikel ->

				pendingArtikelId = artikel.id

				onEvent(
					RezepteEvent.SaveArtikel(artikel)
				)

				onEvent(
					RezepteEvent.ShowArtikelSheet(false)
				)
			},

			onSaveKategorie = { kategorie ->

				onEvent(
					RezepteEvent.SaveKategorie(kategorie)
				)
			},

			onDismiss = {

				onEvent(
					RezepteEvent.ShowArtikelSheet(false)
				)
			}
		)
	}
}

@Preview(showBackground = true)
@Composable
fun RezepteScreenPreview() {
	SmartShoppingTheme {
		RezepteScreen(
			state = RezepteScreenState(
				rezepte = listOf(
					Rezept(
						id = "1",
						name = "Pfannkuchen",
						zubereitungszeit = 15
					),
					Rezept(
						id = "2",
						name = "Spaghetti Bolognese",
						zubereitungszeit = 30
					)
				)
			),
			onEvent = {},
			navController = rememberNavController()
		)
	}
}
