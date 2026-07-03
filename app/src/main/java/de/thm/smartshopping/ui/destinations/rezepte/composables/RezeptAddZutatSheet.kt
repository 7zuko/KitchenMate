package de.thm.smartshopping.ui.destinations.rezepte.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.Artikel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptAddZutatSheet(
    sheetState: SheetState,

    artikelListe: List<Artikel>,

    selectedArtikel: Artikel?,

    onArtikelSelected: (Artikel) -> Unit,

    onConfirm: (Double) -> Unit,

    onDismiss: () -> Unit
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var mengeText by remember {
        mutableStateOf("1")
    }

    val gefilterteArtikel =
        artikelListe.filter {

            searchText.isBlank() ||

                    it.name.contains(
                        searchText,
                        ignoreCase = true
                    )
        }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Zutat hinzufügen",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(

                value = searchText,

                onValueChange = {

                    searchText = it

                },

                label = {

                    Text("Artikel suchen")

                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            LazyColumn(
                modifier = Modifier.height(250.dp)
            ) {

                items(gefilterteArtikel) { artikel ->

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),

                        onClick = {
                            onArtikelSelected(artikel)
                        }
                    ) {

                        Text(
                            text = artikel.name,

                            modifier = Modifier.padding(12.dp),

                            color =
                                if (selectedArtikel?.id == artikel.id)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text =
                    selectedArtikel?.name
                        ?: "Kein Artikel ausgewählt"
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = mengeText,

                onValueChange = {
                    mengeText = it
                },

                label = {
                    Text("Menge")
                }
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                enabled = selectedArtikel != null,

                onClick = {

                    val menge =
                        mengeText.toDoubleOrNull() ?: 1.0

                    onConfirm(menge)
                }
            ) {

                Text("Zutat hinzufügen")
            }
        }
    }
}