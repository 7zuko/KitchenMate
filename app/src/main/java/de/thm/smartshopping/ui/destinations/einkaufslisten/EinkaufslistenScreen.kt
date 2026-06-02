package de.thm.smartshopping.ui.destinations.einkaufslisten

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.thm.smartshopping.DashboardTopAppBar
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.methods.navBarHeight
import de.thm.smartshopping.ui.destinations.einkaufslisten.composables.EinkaufslisteContextMenuSheet
import de.thm.smartshopping.ui.destinations.einkaufslisten.composables.EinkaufslisteCreateSheet
import de.thm.smartshopping.ui.destinations.einkaufslisten.composables.EinkaufslisteEntry
import de.thm.smartshopping.ui.destinations.einkaufslisten.events.EinkaufslistenEvent
import de.thm.smartshopping.ui.destinations.einkaufslisten.states.EinkaufslistenScreenState
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkaufslistenScreen(
	state: EinkaufslistenScreenState,
	onEvent: (EinkaufslistenEvent) -> Unit,
	navController: NavController,
) {

	var searchText by remember {
		mutableStateOf("")
	}

	var isSearching by remember {
		mutableStateOf(false)
	}

	val listState = rememberLazyListState()

	val scope = rememberCoroutineScope()
	val sheetState = rememberModalBottomSheetState(
		skipPartiallyExpanded = true
	)

	LaunchedEffect(state.selectedEinkaufslisteForMenu) {
		scope.launch {
			if (state.selectedEinkaufslisteForMenu != null) {
				sheetState.show()
			} else {
				if (sheetState.isVisible) {
					sheetState.hide()
				}
			}
		}
	}

	LaunchedEffect(sheetState.isVisible) {
		if (!sheetState.isVisible && state.selectedEinkaufslisteForMenu != null) {
			onEvent(EinkaufslistenEvent.DismissContextMenu)
		}
	}

	val createSheetState = rememberModalBottomSheetState(
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

	LaunchedEffect(createSheetState.isVisible) {
		if (!createSheetState.isVisible && state.showCreateSheet) {
			onEvent(EinkaufslistenEvent.ShowCreateSheet(false))
		}
	}

	Scaffold(
		modifier = Modifier.padding(bottom = navBarHeight),
		topBar = {
			DashboardTopAppBar(
				title = if (isSearching) "" else "Einkaufslisten",
				showNavigationIcon = false,
				actions = {
					if (isSearching) {
						OutlinedTextField(
							value = searchText,
							onValueChange = {
								searchText = it
							},
							placeholder = {
								Text("Suchen...")
							},
							singleLine = true
						)
					} else {
						IconButton(
							onClick = {
								isSearching = true
							}
						) {
							Icon(
								Icons.Default.Search,
								contentDescription = "Suche"
							)
						}
					}
					IconButton(onClick = {
						onEvent(EinkaufslistenEvent.ShowCreateSheet(true))
					}) {
						Icon(Icons.Filled.Add, contentDescription = "Einkaufsliste erstellen")
					}
				}
			)
		}
	) { paddingValues ->
		Box(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			if (state.isLoading) {
				CircularProgressIndicator()
			} else {
				if (state.einkaufslisten.isEmpty()) {
					Box(
						modifier = Modifier.fillMaxSize(),
						contentAlignment = Alignment.Center
					) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Text(

								text = "🛒",

								style = MaterialTheme.typography.headlineLarge

							)

							Text(

								text = "Noch keine Einkaufslisten",

								style = MaterialTheme.typography.headlineSmall

							)

							Text(

								text = "Erstelle deine erste Liste über das Plus-Symbol",

								style = MaterialTheme.typography.bodyMedium

							)
						}
					}
				} else {
					val filteredListen =
						state.einkaufslisten.filter {

							searchText.isBlank() ||

									it.name.contains(
										searchText,
										ignoreCase = true
									)
						}

					LazyColumn(
						state = listState,
						modifier = Modifier.fillMaxSize()
					) {
						items(
							items = filteredListen,
							key = { it.id }
						) { einkaufsliste ->
							//Box(Modifier.fillMaxWidth()) {
							EinkaufslisteEntry(
								einkaufsliste = einkaufsliste,
								onClick = {
									navController.navigate("einkaufslisten_ansicht/${einkaufsliste.id}")
								},
								onLongClick = {
									onEvent(EinkaufslistenEvent.ShowContextMenu(einkaufsliste))
								}
							)
						}
					}
				}
			}
		}
	}
	if (state.selectedEinkaufslisteForMenu != null) {
		EinkaufslisteContextMenuSheet(
			einkaufsliste = state.selectedEinkaufslisteForMenu,
			sheetState = sheetState,
			onRenameConfirmed = { list, newName ->
				onEvent(EinkaufslistenEvent.RenameEinkaufsliste(list, newName))
			},
			onDeleteConfirmed = { list ->
				onEvent(EinkaufslistenEvent.DeleteEinkaufsliste(list))
			},
			onDismiss = { onEvent(EinkaufslistenEvent.DismissContextMenu) }
		)
	}

	if (state.showCreateSheet) {
		EinkaufslisteCreateSheet(
			sheetState = createSheetState,
			onDismiss = { onEvent(EinkaufslistenEvent.ShowCreateSheet(false)) },
			onCreateConfirmed = { name ->
				onEvent(EinkaufslistenEvent.ShowCreateSheet(false))
				onEvent(EinkaufslistenEvent.CreateNewEinkaufsliste(name))
			}
		)
	}
}

@Preview(showBackground = true)
@Composable
fun EinkaufslistenScreenPreview() {
	val sampleListen = listOf(
		Einkaufsliste(id = "1", name = "Wocheneinkauf", bearbeitetAm = Date(), erstellerId = "user1"),
		Einkaufsliste(id = "2", name = "Grillparty", bearbeitetAm = Date(System.currentTimeMillis() - 86400000L), erstellerId = "user2"), // Yesterday
		Einkaufsliste(id = "3", name = "Baumarkt", bearbeitetAm = Date(System.currentTimeMillis() - 172800000L), erstellerId = "user1")  // Day before yesterday
	)

	val previewState = EinkaufslistenScreenState(
		einkaufslisten = sampleListen
	)

	SmartShoppingTheme {
		EinkaufslistenScreen(
			state = previewState,
			onEvent = { println("Preview: Event triggered: $it") },
			navController = rememberNavController(),
		)
	}
}