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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

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

    LaunchedEffect(state.showAddZutatSheet) {

        if (state.showAddZutatSheet) {

            onEvent(
                RezepteEvent.LoadAllArtikel
            )
        }
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
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = rezept.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Surface(
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Image(
                        painter = painterResource(
                            R.drawable.ic_placeholder_recipe
                        ),
                        contentDescription = rezept.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {

                    Text(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),

                        text = "⏱ ${rezept.zubereitungszeit} Minuten"
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "Beschreibung",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = rezept.beschreibung ?: ""
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "Zutaten",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (rezept.zutaten.isEmpty()) {

                    Text(
                        text = "Noch keine Zutaten vorhanden",
                        style = MaterialTheme.typography.bodyMedium
                    )

                } else {

                    rezept.zutaten.forEach { zutat ->

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .combinedClickable(
                                    onClick = {
                                        // aktuell nichts
                                    },

                                    onLongClick = {
                                        onEvent(
                                            RezepteEvent.RemoveZutatFromRezept(
                                                rezept.id,
                                                zutat
                                            )
                                        )
                                    }
                                ),

                            shape = RoundedCornerShape(16.dp),

                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {

                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "🥕",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(
                                    modifier = Modifier.width(12.dp)
                                )

                                Text(
                                    text = zutat.artikel.name,
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

                                        text =
                                            "${zutat.menge} ${zutat.artikel.einheit ?: ""}"
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                IconButton(
                                    onClick = {
                                        onEvent(
                                            RezepteEvent.RemoveZutatFromRezept(
                                                rezept.id,
                                                zutat
                                            )
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Zutat löschen"
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {
                        onEvent(
                            RezepteEvent.ShowAddZutatSheet(true)
                        )
                    }
                ) {
                    Text("+ Zutat hinzufügen")
                }
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

            onDismiss = {
                onEvent(
                    RezepteEvent.ShowAddZutatSheet(false)
                )
            }
        )
    }
}