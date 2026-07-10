package de.thm.smartshopping.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import de.thm.smartshopping.data.Artikel
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVorratSheet(
    sheetState: SheetState,
    artikelListe: List<Artikel>,
    selectedArtikel: Artikel?,
    onSave: (Artikel, Double) -> Unit,
    onCreateArtikel: () -> Unit,
    onDismiss: () -> Unit
){
    var searchText by remember {
        mutableStateOf("")
    }

    var mengeText by remember {
        mutableStateOf("")
    }

    var currentSelectedArtikel by remember {
        mutableStateOf<Artikel?>(selectedArtikel)
    }

    LaunchedEffect(selectedArtikel) {

        if (selectedArtikel != null) {
            currentSelectedArtikel = selectedArtikel
            searchText = selectedArtikel.name
        }

    }

    val gefilterteArtikel =
        artikelListe.filter {

            searchText.isBlank() ||

                    it.name.contains(
                        searchText,
                        ignoreCase = true
                    )
        }

    CustomModalSheet(

        title = "Bestand hinzufügen",

        sheetState = sheetState,

        confirmButtonName = "Speichern",

        conditionConfirmEnabled =
            currentSelectedArtikel != null &&
                    mengeText.toDoubleOrNull() != null,

        onConfirmAfterClose = {

            onSave(
                currentSelectedArtikel!!,
                mengeText.toDouble()
            )

        },

        onDismissAfterClose = onDismiss

    ) {
        OutlinedTextField(
            value = searchText,

            onValueChange = {
                searchText = it
            },

            label = {
                Text("Artikel suchen")
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true
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

                        currentSelectedArtikel = artikel

                    }

                ) {

                    Text(

                        text = artikel.emoji + " " + artikel.name,

                        modifier = Modifier.padding(16.dp),

                        color =
                            if (currentSelectedArtikel?.id == artikel.id)
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
                currentSelectedArtikel?.let {

                    "Ausgewählt: ${it.emoji} ${it.name}"

                } ?: "Kein Artikel ausgewählt",

            style = MaterialTheme.typography.bodyMedium
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
                Text("Bestand")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(

            modifier = Modifier.fillMaxWidth(),

            onClick = onCreateArtikel

        ) {

            Text("➕ Neuen Artikel anlegen")

        }
    }
}