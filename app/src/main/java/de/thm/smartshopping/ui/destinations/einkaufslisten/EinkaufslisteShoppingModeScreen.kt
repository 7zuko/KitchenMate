package de.thm.smartshopping.ui.destinations.einkaufslisten

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.text.style.TextDecoration
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EinkaufslistenShoppingModeScreen(
	id: String,
	state: EinkaufslistenAnsichtState,
	onEvent: (EinkaufslistenAnsichtEvent) -> Unit,
	navController: NavController
) {
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute: String? = navBackStackEntry?.destination?.route

	LaunchedEffect(currentRoute) {
		if (currentRoute == "einkaufslisten_shoppingmode/{einkaufslisteId}") {
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
		modifier = Modifier.padding(bottom = 20.dp),
		topBar = {
			DashboardTopAppBar(
				title = {
					Text("${state.einkaufsliste?.name} 🛒")
				},
				showNavigationIcon = true,
				navigationIcon = {
					IconButton(
						onClick = {
							onEvent(EinkaufslistenAnsichtEvent.ExitShoppingMode)

							navController.navigate("einkaufslisten_ansicht/$id") {
								popUpTo("einkaufslisten_ansicht/{einkaufslisteId}") {
									inclusive = true
								}
							}
						}
					) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Zurück"
						)
					}
				},
				actions = {
					IconButton(onClick = {
					}) {
						Icon(Icons.Filled.Search, contentDescription = "Search")
					}
				}
			)
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
		},
		floatingActionButtonPosition = FabPosition.Center
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
						text = "🛍️",
						style = MaterialTheme.typography.headlineLarge
					)

					Text(
						text = "Einkaufsliste leer",
						style = MaterialTheme.typography.headlineSmall
					)

					Text(
						text = "Füge Artikel hinzu um zu starten",
						style = MaterialTheme.typography.bodyMedium
					)
				}
			} else {
				LazyColumn(
					modifier = Modifier.fillMaxSize()
				) {
					state.gruppierteArtikel.forEach { kategorieGruppe ->
						stickyHeader {
							val nichtErledigteArtikelAnzahl = kategorieGruppe.einkaufsArtikelListe.count { !it.erledigt }

							KategorieHeader(
								kategorieName = kategorieGruppe.kategorie.name,
								isExpanded = kategorieGruppe.isExpanded,
								artikelAnzahl = nichtErledigteArtikelAnzahl,
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
									onEvent = onEvent
								)
							}
						}
					}

					if (state.artikelOhneKategorie.isNotEmpty()) {
						stickyHeader {
							val nichtErledigteArtikelAnzahl = state.artikelOhneKategorie.count { !it.erledigt }

							OhneKategorieHeader(artikelAnzahl = nichtErledigteArtikelAnzahl)
						}
						items(
							items = state.artikelOhneKategorie,
							key = { artikel -> "ohne_kat_artikel_${artikel.artikel.id}" }
						) {
							ArtikelEntry(
								artikel = it,
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
			sheetState = sheetEditArtikelState,
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
fun EinkaufslistenShoppingModeScreenPreview() {
	val einkaufsliste = Einkaufsliste(id = "1", name = "Wocheneinkauf", bearbeitetAm = Date(), erstellerId = "user1")

	val previewState = EinkaufslistenAnsichtState(
		einkaufsliste = einkaufsliste
	)

	SmartShoppingTheme {
		EinkaufslistenShoppingModeScreen(id = "1", state = previewState, onEvent = {}, navController = rememberNavController())
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArtikelEntry(
	artikel: EinkaufsArtikel,
	onEvent: (EinkaufslistenAnsichtEvent) -> Unit
) {
	val locale = Locale.GERMANY
	val numberFormat = NumberFormat.getNumberInstance(locale)

	if (numberFormat is DecimalFormat) {
		numberFormat.maximumFractionDigits = 2
		numberFormat.minimumFractionDigits = 0
	}

	val formattedMenge = artikel.menge.formatToDisplay()

	val textDecoration = if (artikel.erledigt) TextDecoration.LineThrough else TextDecoration.None
	val alpha = if (artikel.erledigt) 0.6f else 1.0f

	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 6.dp)
			.combinedClickable(
				onClick = {
					onEvent(
						EinkaufslistenAnsichtEvent.ToggleArtikelErledigt(
							artikel
						)
					)
				},
				onLongClick = {
					onEvent(
						EinkaufslistenAnsichtEvent.ShowEditArtikelMenu(
							artikel
						)
					)
				}
			),

		shape = RoundedCornerShape(20.dp),

		elevation = CardDefaults.elevatedCardElevation(
			defaultElevation = 4.dp
		)
	) {

		Row(
			modifier = Modifier.padding(18.dp),
			verticalAlignment = Alignment.CenterVertically
		) {

			Checkbox(
				modifier = Modifier.size(30.dp),
				checked = artikel.erledigt,
				onCheckedChange = {
					onEvent(
						EinkaufslistenAnsichtEvent.ToggleArtikelErledigt(
							artikel
						)
					)
				}
			)

			Spacer(modifier = Modifier.width(12.dp))

			Column(
				modifier = Modifier.weight(1f)
			) {

				Row(
					verticalAlignment = Alignment.CenterVertically
				) {

					Text(
						text = artikel.artikel.name,
						style = MaterialTheme.typography.titleLarge,
						textDecoration = textDecoration,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
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

					Spacer(modifier = Modifier.height(10.dp))

					Row(
						verticalAlignment = Alignment.CenterVertically
					) {

						Icon(
							modifier = Modifier.size(16.dp),
							imageVector = Icons.AutoMirrored.Outlined.StickyNote2,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.outline.copy(alpha = alpha)
						)

						Spacer(modifier = Modifier.width(8.dp))

						Text(
							text = artikel.notiz,
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.outline.copy(alpha = alpha),
							maxLines = 3
						)
					}
				}
			}
		}
	}
}

@Preview(group = "ArtikelEntity", showBackground = true)
@Composable
private fun ArtikelEntryPreviewErledigt() {
	SmartShoppingTheme {
		ArtikelEntry(
			artikel = EinkaufsArtikel(
				artikel = Artikel(
					id = "1",
					name = "Milch",
					einheit = "Liter"
				),
				menge = 2.0,
				notiz = "Fettarm Montag und Sonntag mit Milch und Butter und Zucker",
				erledigt = true
			),
			onEvent = {}
		)
	}
}
@Preview(group = "ArtikelEntity", showBackground = true)
@Composable
private fun ArtikelEntryPreviewNichtErledigt() {
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
			onEvent = {}
		)
	}
}