package de.thm.smartshopping.ui.destinations.rezepte.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.thm.smartshopping.DashboardTopAppBar
import de.thm.smartshopping.data.Rezept
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import de.thm.smartshopping.R
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptAddZutatSheet
import de.thm.smartshopping.ui.destinations.rezepte.events.RezepteEvent
import de.thm.smartshopping.ui.destinations.rezepte.states.RezepteScreenState
import de.thm.smartshopping.ui.destinations.rezepte.viewmodels.RezepteViewModel
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage
import de.thm.smartshopping.data.RezeptZutat
import de.thm.smartshopping.data.ZutatenStatus
import de.thm.smartshopping.methods.navBarHeight
import de.thm.smartshopping.ui.composables.AddArtikelSheet
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptAddToShoppingListSheet
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptBeschreibungCard
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptEditImageSheet
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptEditNameSheet
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptEditBeschreibungSheet
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptHeroCard
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptInfoCard
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptZutatenCard
import de.thm.smartshopping.ui.destinations.rezepte.events.RezepteUiEvent
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RezeptDetailScreen(
    rezeptId: String,
    state: RezepteScreenState,
    onEvent: (RezepteEvent) -> Unit,
    navController: NavController,
    viewModel: RezepteViewModel = hiltViewModel()
) {

    println("Gesuchte ID: $rezeptId")
    println("Rezepte im State: ${state.rezepte.size}")

    state.rezepte.forEach {
        println("Rezept: ${it.id} -> ${it.name}")
    }

    val rezept =
        state.rezepte.find {
            it.id == rezeptId
        }

    if (rezept == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Rezept nicht gefunden")
        }

        return
    }

    val aktuellesRezept = rezept

    val vorhandeneZutaten =
        aktuellesRezept.zutaten.count {

            state.zutatenStatus[it.artikel.id] ==
                    ZutatenStatus.VORHANDEN

        }

    val teilweiseVorhandeneZutaten =
        aktuellesRezept.zutaten.count {

            state.zutatenStatus[it.artikel.id] ==
                    ZutatenStatus.TEILWEISE

        }

    val fehlendeZutaten =
        aktuellesRezept.zutaten.count {

            state.zutatenStatus[it.artikel.id] ==
                    ZutatenStatus.FEHLT

        }

    val addZutatSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    val artikelSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    var selectedArtikel by remember {
        mutableStateOf(state.selectedArtikelForRezept)
    }

    var pendingArtikelId by remember {
        mutableStateOf<String?>(null)
    }

    var showMenu by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var selectedShoppingZutat by remember {
        mutableStateOf<RezeptZutat?>(null)
    }

    var showShoppingListSheet by remember {
        mutableStateOf(false)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var showEditNameSheet by remember {
        mutableStateOf(false)
    }
    var showEditBeschreibungSheet by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(state.showAddZutatSheet) {

        if (state.showAddZutatSheet) {

            addZutatSheetState.show()

        } else {

            if (addZutatSheetState.isVisible) {
                addZutatSheetState.hide()
            }
        }
    }

    LaunchedEffect(state.showArtikelSheet) {

        if (state.showArtikelSheet) {

            artikelSheetState.show()

        } else {

            if (artikelSheetState.isVisible) {
                artikelSheetState.hide()
            }
        }
    }

    LaunchedEffect(state.allArtikel, pendingArtikelId) {

        val id = pendingArtikelId ?: return@LaunchedEffect

        val neuerArtikel =
            state.allArtikel.find {
                it.id == id
            } ?: return@LaunchedEffect

        selectedArtikel = neuerArtikel

        pendingArtikelId = null

        onEvent(
            RezepteEvent.ShowAddZutatSheet(true)
        )
    }

    LaunchedEffect(Unit) {

        viewModel.uiEvent.collect { event ->

            when (event) {

                is RezepteUiEvent.ShowSnackbar -> {

                    snackbarHostState.showSnackbar(
                        event.message
                    )

                }
            }
        }
    }

    Scaffold(

        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },

        topBar = {

            DashboardTopAppBar(
                title = {
                    Text("Rezept")
                },

                showNavigationIcon = true,

                onNavigationIconClick = {
                    navController.popBackStack()
                },

                actions = {

                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Rezept löschen",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(bottom = navBarHeight)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                RezeptHeroCard(
                    rezept = rezept,
                    onEditName = {
                        showEditNameSheet = true
                    },
                    onEditImage = {

                        onEvent(

                            RezepteEvent.ShowEditImageSheet(true)

                        )

                    }
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                RezeptInfoCard(
                    zubereitungszeit = rezept.zubereitungszeit,
                    anzahlZutaten = aktuellesRezept.zutaten.size,
                    vorhandeneZutaten = vorhandeneZutaten,
                    teilweiseVorhandeneZutaten = teilweiseVorhandeneZutaten,
                    fehlendeZutaten = fehlendeZutaten,
                    schwierigkeit = rezept.schwierigkeit
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                RezeptBeschreibungCard(
                    beschreibung = rezept.beschreibung,
                    onEditClick = {
                        showEditBeschreibungSheet = true
                    }
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                RezeptZutatenCard(
                    zutaten = rezept.zutaten,
                    zutatenStatus = state.zutatenStatus,

                    onAddClick = {
                        onEvent(
                            RezepteEvent.ShowAddZutatSheet(true)
                        )
                    },

                    onDeleteClick = { zutat ->

                        onEvent(
                            RezepteEvent.RemoveZutatFromRezept(
                                rezept.id,
                                zutat
                            )
                        )
                    },

                    onUpdateMenge = { zutat, neueMenge ->

                        val neuesRezept = rezept.copy(
                            zutaten = rezept.zutaten.map {

                                if (it == zutat) {
                                    it.copy(
                                        menge = neueMenge
                                    )
                                } else {
                                    it
                                }
                            }
                        )

                        onEvent(
                            RezepteEvent.UpdateRezept(neuesRezept)
                        )
                    },

                    onAddToShoppingList = { zutat ->

                        selectedShoppingZutat = zutat

                        showShoppingListSheet = true
                    },
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),

                    onClick = {

                        onEvent(
                            RezepteEvent.CreateShoppingListFromRecipe(
                                rezept
                            )
                        )

                    }
                ) {
                    Text("🛒 Einkaufsliste erstellen")
                }

                Spacer(

                    modifier = Modifier.height(150.dp)

                )
            }
        }
    }

    if (showDeleteDialog) {

        AlertDialog(

            onDismissRequest = {
                showDeleteDialog = false
            },

            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,

            title = {
                Text("Rezept löschen?")
            },

            text = {
                Text("Möchtest du dieses Rezept wirklich dauerhaft löschen?")
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        showDeleteDialog = false

                        onEvent(
                            RezepteEvent.DeleteRezept(
                                aktuellesRezept
                            )
                        )

                        navController.popBackStack()
                    }

                ) {

                    Text("Löschen")

                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showDeleteDialog = false
                    }

                ) {

                    Text("Abbrechen")

                }
            }
        )
    }

    if (state.showAddZutatSheet) {

        RezeptAddZutatSheet(
            sheetState = addZutatSheetState,

            artikelListe = state.allArtikel,

            selectedArtikel = selectedArtikel,

            onArtikelSelected = {
                selectedArtikel = it
            },

            onConfirm = { menge ->

                selectedArtikel?.let { artikel ->

                    onEvent(
                        RezepteEvent.AddZutatToRezept(
                            rezeptId = rezept.id,

                            zutat = de.thm.smartshopping.data.RezeptZutat(
                                artikel = artikel,
                                menge = menge
                            )
                        )
                    )

                    selectedArtikel = null

                    onEvent(
                        RezepteEvent.ShowAddZutatSheet(false)
                    )
                }
            },

            onCreateNewArtikel = {

                onEvent(
                    RezepteEvent.ShowAddZutatSheet(false)
                )

                onEvent(
                    RezepteEvent.ShowArtikelSheet(true)
                )
            },

            onDismiss = {
                onEvent(
                    RezepteEvent.ShowAddZutatSheet(false)
                )
            }
        )
    }

    if (showShoppingListSheet && selectedShoppingZutat != null) {

        RezeptAddToShoppingListSheet(

            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),

            einkaufslisten = state.einkaufslisten,

            onNeueListe = {
                // kommt später
            },

            onListSelected = { liste ->

                selectedShoppingZutat?.let { zutat ->

                    onEvent(
                        RezepteEvent.AddArtikelToShoppingList(
                            einkaufsliste = liste,
                            zutat = zutat
                        )
                    )

                    scope.launch {

                        snackbarHostState.showSnackbar(
                            "${zutat.artikel.name} wurde zu \"${liste.name}\" hinzugefügt."
                        )

                    }
                }

                showShoppingListSheet = false
                selectedShoppingZutat = null
            },

            onDismiss = {

                showShoppingListSheet = false

                selectedShoppingZutat = null
            }
        )
    }

    if (showEditNameSheet) {

        RezeptEditNameSheet(

            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),

            rezept = rezept,

            onDismiss = {
                showEditNameSheet = false
            },

            onSave = { neuerName ->

                onEvent(
                    RezepteEvent.UpdateRezept(
                        rezept.copy(
                            name = neuerName
                        )
                    )
                )

                showEditNameSheet = false
            }
        )
    }

    if (showEditBeschreibungSheet) {

        RezeptEditBeschreibungSheet(
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            rezept = rezept,
            onDismiss = {
                showEditBeschreibungSheet = false
            },
            onSave = { neueBeschreibung ->

                onEvent(
                    RezepteEvent.UpdateRezept(
                        rezept.copy(
                            beschreibung = neueBeschreibung
                        )
                    )
                )

                showEditBeschreibungSheet = false
            }
        )
    }

    if (state.showEditImageSheet) {

        RezeptEditImageSheet(

            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),

            onDismiss = {

                onEvent(
                    RezepteEvent.ShowEditImageSheet(false)
                )

            },

            onImageSelected = { bildPfad ->

                onEvent(
                    RezepteEvent.UpdateRezept(
                        rezept.copy(
                            bildPfad = bildPfad
                        )
                    )
                )

                onEvent(
                    RezepteEvent.ShowEditImageSheet(false)
                )
            }

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