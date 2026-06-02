package de.thm.smartshopping.ui.destinations.artikelverwaltung

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import de.thm.smartshopping.methods.enterTransitionDuration
import de.thm.smartshopping.methods.navBarHeight
import de.thm.smartshopping.ui.composables.KategorieHeader
import de.thm.smartshopping.ui.composables.OhneKategorieHeader
import de.thm.smartshopping.ui.destinations.artikelverwaltung.composables.ArtikelVerwaltungAddArtikelSheet
import de.thm.smartshopping.ui.destinations.artikelverwaltung.events.ArtikelVerwaltungEvent
import de.thm.smartshopping.ui.destinations.artikelverwaltung.states.ArtikelVerwaltungState
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArtikelVerwaltungScreen(
	state: ArtikelVerwaltungState,
	onEvent: (ArtikelVerwaltungEvent) -> Unit,
	navController: NavController,
) {
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute: String? = navBackStackEntry?.destination?.route

	LaunchedEffect(currentRoute) {
		if (currentRoute == "artikelverwaltung") {
			delay(enterTransitionDuration.toLong())
			onEvent(ArtikelVerwaltungEvent.SetEnterTransitionFinished(true))
		} else {
			delay(enterTransitionDuration.toLong())
			onEvent(ArtikelVerwaltungEvent.SetEnterTransitionFinished(false))
		}
	}

	val scope = rememberCoroutineScope()

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
			onEvent(ArtikelVerwaltungEvent.ShowAddArtikelMenu(false))
		}
	}

	Scaffold(
		modifier = Modifier.padding(bottom = navBarHeight),
		topBar = {
			DashboardTopAppBar(
				title = "Artikelverwaltung",
				showNavigationIcon = false,
				actions = {
					IconButton(onClick = { println("Search clicked") }) {
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
					onClick = {
						onEvent(ArtikelVerwaltungEvent.ShowAddArtikelMenu(true))
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
				Text(text = "Keine Artikel vorhanden",
					style = MaterialTheme.typography.bodyLarge)
			} else {
				LazyColumn(
					modifier = Modifier.fillMaxSize()
				) {
					state.gruppierteArtikel.forEach { kategorieGruppe ->
						stickyHeader {
							KategorieHeader(
								kategorieName = kategorieGruppe.kategorie.name,
								isExpanded = kategorieGruppe.isExpanded,
								artikelAnzahl = kategorieGruppe.artikelListe.size,
								onClick = {
									onEvent(ArtikelVerwaltungEvent.OnKategorieToggle(kategorieGruppe.kategorie.id))
								}
							)
						}

						if (kategorieGruppe.isExpanded) {
							items(
								items = kategorieGruppe.artikelListe,
								key = { it.id }
							) { artikel ->
								ArtikelZeile(artikel = artikel)
							}
						}
					}

					if (state.artikelOhneKategorie.isNotEmpty()) {
						stickyHeader {
							OhneKategorieHeader(artikelAnzahl = state.artikelOhneKategorie.size)
						}
						items(
							items = state.artikelOhneKategorie,
							key = { artikel -> "ohne_kat_artikel_${artikel.id}" }
						) { artikel ->
							ArtikelZeile(artikel = artikel)
						}
					}
				}
			}
		}
	}

	if (state.showAddArtikelMenu) {
		onEvent(ArtikelVerwaltungEvent.GetAllKategorien)

		ArtikelVerwaltungAddArtikelSheet(
			sheetState = sheetAddArtikelState,
			allArtikel = state.artikelListe,
			allKategorien = state.allKategorien,
			onEvent = onEvent
		)
	}
}

@Preview(showBackground = true)
@Composable
fun ArtikelVerwaltungScreenPreview() {
	val previewState = ArtikelVerwaltungState(
	)

	SmartShoppingTheme {
		ArtikelVerwaltungScreen(
			state = previewState,
			onEvent = {},
			navController = rememberNavController()
		)
	}
}

@Composable
fun ArtikelZeile(
	artikel: Artikel
) {
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 6.dp)
			.clickable { },

		shape = RoundedCornerShape(20.dp),

		elevation = CardDefaults.elevatedCardElevation(
			defaultElevation = 3.dp
		)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(18.dp),

			verticalAlignment = Alignment.CenterVertically
		) {

			Text(
				text = "🥕",
				fontSize = 24.sp
			)

			Spacer(modifier = Modifier.width(12.dp))

			Column(
				modifier = Modifier.weight(1f)
			) {

				Text(
					text = artikel.name,
					style = MaterialTheme.typography.titleLarge
				)

				artikel.einheit?.let {

					Text(
						text = it,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun ArtikelZeilePreview() {
	SmartShoppingTheme {
		ArtikelZeile(
			artikel = Artikel(
				id = "1",
				name = "Milch",
				kategorie = null,
				einheit = "Liter"
			)
		)
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArtikelEntry( //NICHT BENUTZT
	artikel: Artikel,
	isSelected: Boolean,
	isInSelectionMode: Boolean,
	onEvent: (ArtikelVerwaltungEvent) -> Unit,
) {
	val backgroundColor = if (isSelected) {
		MaterialTheme.colorScheme.primaryContainer
	} else {
		MaterialTheme.colorScheme.background
	}

	Column(
		modifier = Modifier
			.height(50.dp)
			.fillMaxWidth()
			.background(backgroundColor)
			.combinedClickable(
				onClick = {
					if (isInSelectionMode) {
						//onEvent(EinkaufslistenAnsichtEvent.ToggleArtikelSelection(artikel.artikel.id))
					} else {
						//onEvent(EinkaufslistenAnsichtEvent.ShowEditArtikelMenu(artikel))
					}
				},
				onLongClick = {
					//onEvent(EinkaufslistenAnsichtEvent.LongPressArtikel(artikel.artikel.id))
				}

			)
			.padding(horizontal = 24.dp),
		verticalArrangement = Arrangement.Center
	) {
		Row(
			modifier = Modifier
				.padding(vertical = 8.dp)
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Start
		) {
			Text(
				modifier = Modifier
					.weight(3f),
				text = artikel.name,
				fontSize = 20.sp
			)
			Text(
				modifier = Modifier
					.padding(start = 12.dp)
					.weight(2f),
				text = artikel.einheit ?: "-",
				fontSize = 20.sp
			)
		}
	}
}

@Preview(group = "ArtikelEntity", showBackground = true)
@Composable
fun ArtikelEntryPreview() {
	SmartShoppingTheme {
		ArtikelEntry(
			artikel = Artikel(
				id = "1",
				name = "Milch",
				kategorie = null,
				einheit = "Liter"
			),
			isSelected = false,
			isInSelectionMode = false,
			onEvent = {}
		)
	}
}

@Composable
private fun ColumnBezeichnungen() {
	val textsize = 12.sp
	Row(
		modifier = Modifier
			.height(25.dp)
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.secondary),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Start
	) {
		Text(
			modifier = Modifier
				.padding(start = 24.dp)
				.weight(3f),
			text = "Artikelname",
			color = MaterialTheme.colorScheme.onSecondary,
			fontSize = textsize
		)
		Text(
			modifier = Modifier
				.padding(start = 12.dp)
				.weight(2f),
			color = MaterialTheme.colorScheme.onSecondary,
			text = "Einheit",
			fontSize = textsize
		)
	}
}

@Preview(showBackground = true)
@Composable
fun ColumnBezeichnungenPreview() {
	SmartShoppingTheme {
		ColumnBezeichnungen()
	}
}