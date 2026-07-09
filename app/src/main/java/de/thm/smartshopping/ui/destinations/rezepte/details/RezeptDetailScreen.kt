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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import coil.compose.AsyncImage
import de.thm.smartshopping.methods.navBarHeight
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptBeschreibungCard
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptHeroCard
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptInfoCard
import de.thm.smartshopping.ui.destinations.rezepte.composables.RezeptZutatenCard
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RezeptDetailScreen(
    rezeptId: String,
    state: RezepteScreenState,
    onEvent: (RezepteEvent) -> Unit,
    navController: NavController
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

    val addZutatSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    if (rezept == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Rezept nicht gefunden")
        }

        return
    }



    Scaffold(

        topBar = {

            DashboardTopAppBar(
                title = {
                    Text("Rezept")
                },

                showNavigationIcon = true,

                onNavigationIconClick = {
                    navController.popBackStack()
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
                    rezept = rezept
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                RezeptInfoCard(
                    zubereitungszeit = rezept.zubereitungszeit,
                    anzahlZutaten = rezept.zutaten.size
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                RezeptBeschreibungCard(
                    beschreibung = rezept.beschreibung
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                RezeptZutatenCard(
                    zutaten = rezept.zutaten,

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
                    }
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

    if (state.showAddZutatSheet) {

        RezeptAddZutatSheet(
            sheetState = addZutatSheetState,

            artikelListe = state.allArtikel,

            selectedArtikel = state.selectedArtikelForRezept,

            onArtikelSelected = { artikel ->

                onEvent(
                    RezepteEvent.SelectArtikelForRezept(
                        artikel
                    )
                )
            },

            onConfirm = { menge ->

                state.selectedArtikelForRezept?.let { artikel ->

                    onEvent(
                        RezepteEvent.AddZutatToRezept(
                            rezeptId = rezept.id,

                            zutat = de.thm.smartshopping.data.RezeptZutat(
                                artikel = artikel,
                                menge = menge
                            )
                        )
                    )

                    onEvent(
                        RezepteEvent.ClearSelectedArtikelForRezept
                    )

                    onEvent(
                        RezepteEvent.ShowAddZutatSheet(false)
                    )
                }
            },

            onCreateNewArtikel = {
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
}