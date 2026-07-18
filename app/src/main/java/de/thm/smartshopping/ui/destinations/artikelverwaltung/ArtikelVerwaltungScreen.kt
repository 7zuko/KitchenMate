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
import de.thm.smartshopping.ui.composables.AddArtikelSheet
import de.thm.smartshopping.ui.destinations.artikelverwaltung.events.ArtikelVerwaltungEvent
import de.thm.smartshopping.ui.destinations.artikelverwaltung.states.ArtikelVerwaltungState
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.thm.smartshopping.ui.composables.SearchTopBar
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import de.thm.smartshopping.data.VorratsArtikel
import de.thm.smartshopping.ui.destinations.artikelverwaltung.composables.AddVorratSheet
import de.thm.smartshopping.ui.destinations.artikelverwaltung.composables.EditVorratSheet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArtikelVerwaltungScreen(
	state: ArtikelVerwaltungState,
	onEvent: (ArtikelVerwaltungEvent) -> Unit,
	navController: NavController,
) {
	var isSearching by remember {
		mutableStateOf(false)
	}

	var searchText by remember {
		mutableStateOf("")
	}

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

	val sheetAddVorratState = rememberModalBottomSheetState(
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

	LaunchedEffect(state.showAddVorratSheet) {
		scope.launch {
			if (state.showAddVorratSheet) {
				sheetAddVorratState.show()
			} else {
				if (sheetAddVorratState.isVisible) {
					sheetAddVorratState.hide()
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
				title = {
					if (!isSearching) {
						Text("Mein Vorrat")
					}
				},
				showNavigationIcon = false,
				actions = {

					if (isSearching) {

						SearchTopBar(
							searchText = searchText,
							placeholder = "Artikel suchen",

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
								Icons.Default.Search,
								contentDescription = "Suche"
							)
						}
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
						onEvent(
							ArtikelVerwaltungEvent.ShowAddVorratSheet(true)
						)
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
						text = "🥕",
						style = MaterialTheme.typography.headlineLarge
					)

					Text(
						text = "Noch keine Artikel",
						style = MaterialTheme.typography.headlineSmall
					)

					Text(
						text = "Füge deinen ersten Artikel hinzu",
						style = MaterialTheme.typography.bodyMedium
					)
				}
			} else {
				val gefilterteGruppen =
					state.gruppierteArtikel.map { gruppe ->
						gruppe.copy(
							artikelListe = gruppe.artikelListe.filter {
								searchText.isBlank() ||
										it.artikel.name.contains(
											searchText,
											ignoreCase = true
										)
							}
						)
					}

				val gefilterteArtikelOhneKategorie =
					state.artikelOhneKategorie.filter {

						searchText.isBlank() ||

								it.artikel.name.contains(
									searchText,
									ignoreCase = true
								)
					}

				val hatTreffer =
					gefilterteGruppen.any { it.artikelListe.isNotEmpty() } ||
							gefilterteArtikelOhneKategorie.isNotEmpty()

				if (!hatTreffer) {
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
								text = "Keine Artikel gefunden",
								style = MaterialTheme.typography.headlineSmall
							)
							Text(
								text = "Versuche einen anderen Suchbegriff",
								style = MaterialTheme.typography.bodyMedium
							)
						}
					}
				} else {
					LazyColumn(
						modifier = Modifier.fillMaxSize()
					) {
						gefilterteGruppen
							.filter {
								it.artikelListe.isNotEmpty()
							}
							.forEach { kategorieGruppe ->
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
										key = { it.artikel.id }
									) { vorratsArtikel ->

										ArtikelZeile(
											artikel = vorratsArtikel.artikel,
											bestand = vorratsArtikel.menge,
											onEvent = onEvent
										)

									}
								}
							}

						if (state.artikelOhneKategorie.isNotEmpty()) {

							stickyHeader {
								OhneKategorieHeader(
									artikelAnzahl = gefilterteArtikelOhneKategorie.size
								)
							}

							items(
								items = gefilterteArtikelOhneKategorie,
								key = { "ohne_kat_artikel_${it.artikel.id}" }
							) { vorratsArtikel ->

								ArtikelZeile(
									artikel = vorratsArtikel.artikel,
									bestand = vorratsArtikel.menge,
									onEvent = onEvent
								)

							}
						}
					}
				}
			}
		}
	}

	if (state.showAddArtikelMenu) {

		onEvent(ArtikelVerwaltungEvent.GetAllKategorien)

		AddArtikelSheet(

			sheetState = sheetAddArtikelState,

			currentArtikel = state.currentArtikel,

			allArtikel = state.artikelListe,

			allKategorien = state.allKategorien,

			onSaveArtikel = { artikel ->

				if (state.currentArtikel != null) {

					onEvent(
						ArtikelVerwaltungEvent.EditArtikel(artikel)
					)

				} else {

					onEvent(
						ArtikelVerwaltungEvent.SaveArtikel(artikel)
					)
				}
			},

			onSaveKategorie = { kategorie ->

				onEvent(
					ArtikelVerwaltungEvent.SaveKategorie(kategorie)
				)

			},

			onDismiss = {

				onEvent(
					ArtikelVerwaltungEvent.ClearCurrentArtikel
				)

				onEvent(
					ArtikelVerwaltungEvent.ShowAddArtikelMenu(false)
				)
			}
		)
	}

	if (state.showAddVorratSheet) {

		AddVorratSheet(

			sheetState = sheetAddVorratState,

			artikelListe = state.artikelListe,

			selectedArtikel = state.selectedArtikelForVorrat,

			onSave = { artikel, menge ->
				onEvent(
					ArtikelVerwaltungEvent.SaveVorrat(
						artikel,
						menge
					)
				)
			},

			onCreateArtikel = {
				onEvent(
					ArtikelVerwaltungEvent.ShowAddVorratSheet(false)
				)

				onEvent(
					ArtikelVerwaltungEvent.ShowAddArtikelMenu(true)
				)
			},

			onDismiss = {
				onEvent(
					ArtikelVerwaltungEvent.ShowAddVorratSheet(false)
				)
			}
		)
	}

	if (state.showEditVorratSheet && state.artikelZumBearbeiten != null) {

		EditVorratSheet(

			sheetState = rememberModalBottomSheetState(
				skipPartiallyExpanded = true
			),

			artikel = state.artikelZumBearbeiten.artikel,

			aktuelleMenge = state.artikelZumBearbeiten.menge,

			onDismiss = {

				onEvent(
					ArtikelVerwaltungEvent.CloseEditVorratSheet
				)

			},

			onSave = { neueMenge ->

				onEvent(
					ArtikelVerwaltungEvent.SaveVorrat(
						artikel = state.artikelZumBearbeiten.artikel,
						menge = neueMenge
					)
				)

				onEvent(
					ArtikelVerwaltungEvent.CloseEditVorratSheet
				)
			}
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
	artikel: Artikel,
	bestand: Double?,
	onEvent: (ArtikelVerwaltungEvent) -> Unit
) {
	var expanded by remember {
		mutableStateOf(false)
	}

	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp)
			.clickable { },

		shape = RoundedCornerShape(28.dp),

		colors = CardDefaults.elevatedCardColors(
			containerColor = MaterialTheme.colorScheme.surface
		),

		elevation = CardDefaults.elevatedCardElevation(
			defaultElevation = 6.dp
		)
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp),
			verticalAlignment = Alignment.Top
		) {

			Box(
				modifier = Modifier
					.size(72.dp)
					.background(
						MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
						CircleShape
					),
				contentAlignment = Alignment.Center
			) {
				Text(
					text = artikel.emoji,
					fontSize = 38.sp
				)
			}

			Spacer(modifier = Modifier.width(20.dp))

			Column(
				modifier = Modifier.weight(1f)
			) {

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {

					Text(
						text = artikel.name,
						style = MaterialTheme.typography.titleLarge,
						modifier = Modifier.weight(1f)
					)

					Box {

						IconButton(
							onClick = {
								expanded = true
							}
						) {
							Icon(
								Icons.Default.MoreVert,
								contentDescription = "Menü"
							)
						}

						DropdownMenu(
							expanded = expanded,
							onDismissRequest = {
								expanded = false
							},
							containerColor = MaterialTheme.colorScheme.surface
						) {

							DropdownMenuItem(
								text = { Text("Bearbeiten") },
								onClick = {
									expanded = false
									onEvent(ArtikelVerwaltungEvent.SetCurrentArtikel(artikel))
									onEvent(ArtikelVerwaltungEvent.ShowAddArtikelMenu(true))
								}
							)

							DropdownMenuItem(
								text = {
									Text(
										"Aus Vorrat entfernen",
										color = MaterialTheme.colorScheme.error
									)
								},
								onClick = {
									expanded = false

									onEvent(
										ArtikelVerwaltungEvent.DeleteVorrat(
											artikel.id
										)
									)
								}
							)
						}
					}
				}


				Spacer(Modifier.height(10.dp))

				HorizontalDivider(
					thickness = 2.dp,
					color = MaterialTheme.colorScheme.outlineVariant
				)

				Spacer(Modifier.height(16.dp))

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {

					Row(
						verticalAlignment = Alignment.CenterVertically
					) {

						Text(
							text = "\uD83D\uDCE6",
							fontSize = 20.sp
						)

						Spacer(Modifier.width(8.dp))

						Column {

							Text(
								text = "Bestand",
								style = MaterialTheme.typography.labelSmall,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)

							Text(
								text = "${bestand ?: 0} ${artikel.einheit ?: ""}",
								style = MaterialTheme.typography.titleMedium
							)
						}
					}

					ElevatedCard(

						modifier = Modifier.clickable {

							onEvent(
								ArtikelVerwaltungEvent.EditVorrat(
									VorratsArtikel(
										artikel = artikel,
										menge = bestand ?: 0.0
									)
								)
							)

						},

						shape = RoundedCornerShape(50.dp),

						colors = CardDefaults.elevatedCardColors(
							containerColor = MaterialTheme.colorScheme.primaryContainer
						),

						elevation = CardDefaults.elevatedCardElevation(
							defaultElevation = 0.dp
						)

					) {

						Row(

							modifier = Modifier.padding(
								horizontal = 10.dp,
								vertical = 5.dp
							),

							verticalAlignment = Alignment.CenterVertically

						) {

							Icon(
								imageVector = Icons.Outlined.Edit,
								contentDescription = null,
								modifier = Modifier.size(18.dp)
							)
						}
					}
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
				einheit = "Liter",
				emoji = "🥛"
			),
			bestand = 2.0,
			onEvent = {}
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