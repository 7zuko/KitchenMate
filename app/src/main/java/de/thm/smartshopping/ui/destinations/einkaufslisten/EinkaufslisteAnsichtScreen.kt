package de.thm.smartshopping.ui.destinations.einkaufslisten

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.thm.smartshopping.DashboardTopAppBar
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.EinkaufsArtikel
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.methods.enterTransitionDuration
import de.thm.smartshopping.methods.formatToDisplay
import de.thm.smartshopping.methods.navBarHeight
import de.thm.smartshopping.ui.composables.KategorieHeader
import de.thm.smartshopping.ui.composables.OhneKategorieHeader
import de.thm.smartshopping.ui.destinations.einkaufslisten.composables.EinkaufslisteAddArtikelSheet
import de.thm.smartshopping.ui.destinations.einkaufslisten.composables.EinkaufslisteContextMenuSheet
import de.thm.smartshopping.ui.destinations.einkaufslisten.composables.EinkaufslisteEditArtikelSheet
import de.thm.smartshopping.ui.destinations.einkaufslisten.events.EinkaufslistenAnsichtEvent
import de.thm.smartshopping.ui.destinations.einkaufslisten.states.EinkaufslistenAnsichtState
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EinkaufslistenAnsicht(
	id: String,
	state: EinkaufslistenAnsichtState,
	onEvent: (EinkaufslistenAnsichtEvent) -> Unit,
	navController: NavController
) {
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute: String? = navBackStackEntry?.destination?.route

	LaunchedEffect(currentRoute) {
		if (currentRoute == "einkaufslisten_ansicht/{einkaufslisteId}") {
			delay(enterTransitionDuration.toLong())
			onEvent(EinkaufslistenAnsichtEvent.SetEnterTransitionFinished(true))
		} else {
			delay(enterTransitionDuration.toLong())
			onEvent(EinkaufslistenAnsichtEvent.SetEnterTransitionFinished(false))
		}
	}

	LaunchedEffect(Unit) {
		onEvent(EinkaufslistenAnsichtEvent.LoadEinkaufsliste(id))
	}

	val scope = rememberCoroutineScope()

	val sheetState = rememberModalBottomSheetState(
		skipPartiallyExpanded = true
	)

	LaunchedEffect(state.showActionMenu) {
		scope.launch {
			if (state.showActionMenu) {
				sheetState.show()
			} else {
				if (sheetState.isVisible) {
					sheetState.hide()
				}
			}
		}
	}
	LaunchedEffect(sheetState.isVisible) {
		if (!sheetState.isVisible && state.showActionMenu) {
			onEvent(EinkaufslistenAnsichtEvent.DismissContextMenu)
		}
	}

	val sheetAddArtikelState = rememberModalBottomSheetState(
		skipPartiallyExpanded = true
	)

	LaunchedEffect(state.showAddArtikelMenu) {
		scope.launch {
			if (state.showAddArtikelMenu) {
				sheetAddArtikelState.show()
			} else {
				if (sheetAddArtikelState.isVisible) {
					sheetAddArtikelState.hide()
				}
			}
		}
	}
	LaunchedEffect(sheetAddArtikelState.isVisible) {
		if (!sheetAddArtikelState.isVisible && state.showAddArtikelMenu) {
			onEvent(EinkaufslistenAnsichtEvent.ShowAddArtikelMenu(false))
		}
	}

	val sheetEditArtikelState = rememberModalBottomSheetState(
		skipPartiallyExpanded = true
	)

	LaunchedEffect(state.showEditArtikelMenu) {
		scope.launch {
			if (state.showEditArtikelMenu != null) {
				sheetEditArtikelState.show()
			} else {
				if (sheetEditArtikelState.isVisible) {
					sheetEditArtikelState.hide()
				}
			}
		}
	}
	LaunchedEffect(sheetEditArtikelState.isVisible) {
		if (!sheetEditArtikelState.isVisible && state.showEditArtikelMenu != null) {
			onEvent(EinkaufslistenAnsichtEvent.DismissEditArtikelMenu)
		}
	}

	Scaffold(
		modifier = Modifier.padding(bottom = navBarHeight),
		topBar = {
			if (state.isInSelectionMode) {
				DashboardTopAppBar(
					title = "${state.selectedArtikelIds.size} ausgewählt",
					showNavigationIcon = true,
					navigationIcon = {
						IconButton(onClick = { onEvent(EinkaufslistenAnsichtEvent.ExitSelectionMode) }) {
							Icon(
								imageVector = Icons.Filled.Close,
								contentDescription = "Abbrechen"
							)
						}
					},
					actions = {
						IconButton(onClick = {
							onEvent(EinkaufslistenAnsichtEvent.DeleteSelectedArtikel)
						}) {
							Icon(Icons.Filled.Delete, contentDescription = "Ausgewählte Artikel löschen")
						}
					}
				)
			} else {
				DashboardTopAppBar(
					title = state.einkaufsliste?.name ?: "",
					showNavigationIcon = true,
					onNavigationIconClick = {
						if (navController.previousBackStackEntry?.destination?.route == "einkaufslisten_shoppingmode/{einkaufslisteId}") {
							navController.navigate("einkaufslisten") {
								popUpTo("einkaufslisten_shoppingmode/{einkaufslisteId}") {
									inclusive = true
								}
							}
						} else {
							navController.popBackStack()
						}
					},
					actions = {
						IconButton(onClick = {
						}) {
							Icon(Icons.Filled.Search, contentDescription = "Search")
						}
						IconButton(onClick = {
							onEvent(EinkaufslistenAnsichtEvent.EnterShoppingMode)
							navController.navigate("einkaufslisten_shoppingmode/$id")
						}) {
							Icon(Icons.Filled.ShoppingCart, contentDescription = "Shopping Modus aktivieren")
						}
						IconButton(onClick = {
							onEvent(EinkaufslistenAnsichtEvent.ShowContextMenu)
						}) {
							Icon(Icons.Filled.Edit, contentDescription = "Einkaufsliste bearbeiten")
						}
					}
				)
			}
		},
		floatingActionButton = {
			AnimatedVisibility(
				visible = !state.isInSelectionMode && state.isEnterTransitionFinished,
				enter = slideInVertically { fullHeight -> fullHeight },
				exit = scaleOut(tween(50))
			) {
				FloatingActionButton(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary,
					onClick = {
						onEvent(EinkaufslistenAnsichtEvent.ShowAddArtikelMenu(true))
					}
				) {
					Icon(Icons.Default.Add, contentDescription = "Artikel hinzufügen")
				}
			}
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
			} else if (state.gruppierteArtikel.isEmpty() && state.artikelOhneKategorie.isEmpty()) {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally
				) {

					Text(
						text = "🛒",
						style = MaterialTheme.typography.headlineLarge
					)

					Text(
						text = "Noch keine Artikel",
						style = MaterialTheme.typography.headlineSmall
					)

					Text(
						text = "Füge Artikel über den Plus-Button hinzu",
						style = MaterialTheme.typography.bodyMedium
					)
				}
			} else {
				LazyColumn(
					modifier = Modifier.fillMaxSize()
				) {
					state.gruppierteArtikel.forEach { kategorieGruppe ->
						stickyHeader {
							KategorieHeader(
								kategorieName = kategorieGruppe.kategorie.name,
								isExpanded = kategorieGruppe.isExpanded,
								artikelAnzahl = kategorieGruppe.einkaufsArtikelListe.size,
								onClick = {
									onEvent(EinkaufslistenAnsichtEvent.OnKategorieToggle(kategorieGruppe.kategorie.id))
								}
							)
						}

						if(kategorieGruppe.isExpanded) {
							items(
								items = kategorieGruppe.einkaufsArtikelListe,
								key = { it.artikel.id }
							) { artikel ->
								ArtikelEntry(
									artikel = artikel,
									isSelected = state.selectedArtikelIds.contains(artikel.artikel.id),
									isInSelectionMode = state.isInSelectionMode,
									onEvent = onEvent
								)
							}
						}
					}

					if (state.artikelOhneKategorie.isNotEmpty()) {
						stickyHeader {
							OhneKategorieHeader(artikelAnzahl = state.artikelOhneKategorie.size)
						}
						items(
							items = state.artikelOhneKategorie,
							key = { artikel -> "ohne_kat_artikel_${artikel.artikel.id}" }
						) {
							ArtikelEntry(
								artikel = it,
								isSelected = state.selectedArtikelIds.contains(it.artikel.id),
								isInSelectionMode = state.isInSelectionMode,
								onEvent = onEvent
							)
						}
					}
				}
			}
		}
	}
	if (state.showActionMenu && state.einkaufsliste != null) {
		EinkaufslisteContextMenuSheet(
			einkaufsliste = state.einkaufsliste,
			sheetState = sheetState,
			onRenameConfirmed = { list, newName ->
				onEvent(EinkaufslistenAnsichtEvent.RenameEinkaufsliste(newName))
			},
			onDeleteConfirmed = { list ->
				navController.popBackStack()
				onEvent(EinkaufslistenAnsichtEvent.DeleteEinkaufsliste)
			},
			onDismiss = { onEvent(EinkaufslistenAnsichtEvent.DismissContextMenu) }
		)
	}

	if (state.showAddArtikelMenu && state.einkaufsliste != null) {
		onEvent(EinkaufslistenAnsichtEvent.LoadAllArtikel)

		EinkaufslisteAddArtikelSheet(
			sheetState = sheetAddArtikelState,
			einkaufsliste = state.einkaufsliste,
			allArtikel = state.allArtikel,
			onConfirmed = { einkaufsArtikel ->
				if (state.einkaufsliste.artikel.contains(einkaufsArtikel)) {

				} else {
					onEvent(EinkaufslistenAnsichtEvent.AddEinkaufsArtikel(einkaufsArtikel))
				}
			},
			onDismiss = { onEvent(EinkaufslistenAnsichtEvent.ShowAddArtikelMenu(false)) }
		)
	}

	if (state.showEditArtikelMenu != null) {

		EinkaufslisteEditArtikelSheet(
			sheetState = sheetAddArtikelState,
			einkaufsArtikel = state.showEditArtikelMenu,
			onConfirmed = { einkaufsArtikel ->
				onEvent(EinkaufslistenAnsichtEvent.AddEinkaufsArtikel(einkaufsArtikel))
			},
			onDismiss = { onEvent(EinkaufslistenAnsichtEvent.DismissEditArtikelMenu) }
		)
	}
}

@Preview(showBackground = true)
@Composable
fun EinkaufslistenAnsichtPreview() {
	val einkaufsliste = Einkaufsliste(id = "1", name = "Wocheneinkauf", bearbeitetAm = Date(), erstellerId = "user1")

	val previewState = EinkaufslistenAnsichtState(
		einkaufsliste = einkaufsliste
	)

	SmartShoppingTheme {
		EinkaufslistenAnsicht(id = "1", state = previewState, onEvent = {}, navController = rememberNavController())
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArtikelEntry(
	artikel: EinkaufsArtikel,
	isSelected: Boolean,
	isInSelectionMode: Boolean,
	onEvent: (EinkaufslistenAnsichtEvent) -> Unit
) {
	val locale = Locale.GERMANY
	val numberFormat = NumberFormat.getNumberInstance(locale)

	if (numberFormat is DecimalFormat) {
		numberFormat.maximumFractionDigits = 2
		numberFormat.minimumFractionDigits = 0
	}

	val formattedMenge = artikel.menge.formatToDisplay()

	val cardColor = if (isSelected) {
		MaterialTheme.colorScheme.primaryContainer
	} else {
		MaterialTheme.colorScheme.surface
	}

	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 6.dp)
			.combinedClickable(
				onClick = {
					if (isInSelectionMode) {
						onEvent(
							EinkaufslistenAnsichtEvent.ToggleArtikelSelection(
								artikel.artikel.id
							)
						)
					} else {
						onEvent(
							EinkaufslistenAnsichtEvent.ShowEditArtikelMenu(
								artikel
							)
						)
					}
				},
				onLongClick = {
					onEvent(
						EinkaufslistenAnsichtEvent.LongPressArtikel(
							artikel.artikel.id
						)
					)
				}
			),

		shape = RoundedCornerShape(20.dp),

		colors = CardDefaults.elevatedCardColors(
			containerColor = cardColor
		)
	) {

		Column(
			modifier = Modifier.padding(18.dp)
		) {

			Row(
				verticalAlignment = Alignment.CenterVertically
			) {

				Text(
					text = artikel.artikel.name,
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier.weight(1f)
				)

				Surface(
					shape = RoundedCornerShape(12.dp),
					color = MaterialTheme.colorScheme.secondaryContainer
				) {

					Text(
						modifier = Modifier.padding(
							horizontal = 10.dp,
							vertical = 6.dp
						),

						text = "$formattedMenge ${artikel.artikel.einheit ?: ""}",
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}

			if (artikel.notiz != null) {

				Spacer(modifier = Modifier.height(12.dp))

				Row(
					verticalAlignment = Alignment.CenterVertically
				) {

					Icon(
						modifier = Modifier.size(18.dp),
						imageVector = Icons.AutoMirrored.Outlined.StickyNote2,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.outline
					)

					Spacer(modifier = Modifier.width(8.dp))

					Text(
						text = artikel.notiz,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.outline
					)
				}
			}
		}
	}
}

@Preview(group = "ArtikelEntity", showBackground = true)
@Composable
private fun ArtikelEntryPreviewNotiz() {
	SmartShoppingTheme {
		ArtikelEntry(
			artikel = EinkaufsArtikel(
				artikel = Artikel(
					id = "1",
					name = "Milch",
					einheit = "Liter"
				),
				menge = 2.0,
				notiz = "Fettarm Montag und Sonntag mit Milch und Butter und Zucker"
			),
			isSelected = false,
			isInSelectionMode = false,
			onEvent = {}
		)
	}
}
@Preview(group = "ArtikelEntity", showBackground = true)
@Composable
private fun ArtikelEntryPreviewNoNotiz() {
	SmartShoppingTheme {
		ArtikelEntry(
			artikel = EinkaufsArtikel(
				artikel = Artikel (
					id = "1",
					name = "Milch",
					einheit = "Liter"
				),
				menge = 2.0
			),
			isSelected = false,
			isInSelectionMode = false,
			onEvent = {}
		)
	}
}