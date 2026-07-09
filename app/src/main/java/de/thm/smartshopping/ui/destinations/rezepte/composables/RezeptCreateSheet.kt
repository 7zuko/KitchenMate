package de.thm.smartshopping.ui.destinations.rezepte.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.composables.CustomModalSheet
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.data.RezeptZutat
import de.thm.smartshopping.methods.ImageUtils
import de.thm.smartshopping.ui.destinations.artikelverwaltung.composables.ArtikelVerwaltungAddArtikelSheet
import de.thm.smartshopping.ui.destinations.artikelverwaltung.events.ArtikelVerwaltungEvent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptCreateSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onCreateConfirmed: (
        name: String,
        beschreibung: String?,
        zubereitungszeit: Int,
        bildPfad: String?,
        kategorie: String,
        schwierigkeit: String,
        zutaten: List<RezeptZutat>
    ) -> Unit,
    allArtikel: List<Artikel>,
    allKategorien: List<ArtikelKategorie>,
    showArtikelSheet: Boolean,
    onShowArtikelSheet: (Boolean) -> Unit,
    onArtikelEvent: (ArtikelVerwaltungEvent) -> Unit,
) {

    var name by remember {
        mutableStateOf("")
    }

    var beschreibung by remember {
        mutableStateOf("")
    }

    var zubereitungszeit by remember {
        mutableStateOf("")
    }

    val kategorien = listOf(
        "Hauptgericht",
        "Vorspeise",
        "Nachspeise",
        "Snack",
        "Frühstück",
        "Getränk"
    )

    val schwierigkeiten = listOf(
        "Einfach",
        "Mittel",
        "Schwer"
    )

    var kategorie by remember { mutableStateOf(kategorien.first()) }

    var schwierigkeit by remember { mutableStateOf(schwierigkeiten.first()) }

    var zutaten by remember {
        mutableStateOf<List<RezeptZutat>>(emptyList())
    }

    var showAddZutatSheet by remember {
        mutableStateOf(false)
    }

    var selectedArtikel by remember {
        mutableStateOf<Artikel?>(null)
    }

    var kategorieExpanded by remember { mutableStateOf(false) }

    var schwierigkeitExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            imageUri = uri
        }

    CustomModalSheet(
        title = "Neues Rezept",
        confirmButtonName = "Speichern",
        sheetState = sheetState,

        conditionConfirmEnabled = name.isNotBlank(),

        onConfirmAfterClose = {

            val bildPfad =
                imageUri?.let {
                    ImageUtils.saveImageToInternalStorage(
                        context,
                        it
                    )
                }

            onCreateConfirmed(
                name.trim(),
                beschreibung.trim().ifBlank { null },
                zubereitungszeit.toIntOrNull() ?: 0,
                bildPfad,
                kategorie,
                schwierigkeit,
                zutaten
            )
        },

        onDismissAfterClose = {
            name = ""
            beschreibung = ""
            zubereitungszeit = ""
            onDismiss()
        }
    ) {
        OutlinedButton(
            onClick = {
                imagePicker.launch("image/*")
            }
        ) {
            Text("📷 Bild auswählen")
        }

        imageUri?.let {

            AsyncImage(
                model = it,
                contentDescription = null,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(top = 12.dp),

                contentScale = ContentScale.Crop
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Rezeptname*")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = beschreibung,
            onValueChange = {
                beschreibung = it
            },
            label = {
                Text("Beschreibung")
            },
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = zubereitungszeit,
            onValueChange = {
                zubereitungszeit = it
            },
            label = {
                Text("Zubereitungszeit (Minuten)")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = kategorieExpanded,
            onExpandedChange = {
                kategorieExpanded = !kategorieExpanded
            },
        ) {
            OutlinedTextField(
                value = kategorie,
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategorie") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = kategorieExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,

                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )

            ExposedDropdownMenu(
                expanded = kategorieExpanded,
                onDismissRequest = { kategorieExpanded = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                kategorien.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.onSurface
                            )},
                        onClick = {
                            kategorie = it
                            kategorieExpanded = false
                        }
                    )
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = schwierigkeitExpanded,
            onExpandedChange = {
                schwierigkeitExpanded = !schwierigkeitExpanded
            }
        ) {
            OutlinedTextField(
                value = schwierigkeit,
                onValueChange = {},
                readOnly = true,
                label = { Text("Schwierigkeit") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = schwierigkeitExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            ExposedDropdownMenu(
                expanded = schwierigkeitExpanded,
                onDismissRequest = { schwierigkeitExpanded = false }
            ) {
                schwierigkeiten.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            schwierigkeit = it
                            schwierigkeitExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        zutaten.forEach { zutat ->

            Text(
                text = "• ${zutat.artikel.name} (${zutat.menge} ${zutat.artikel.einheit ?: ""})"
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "🥕 Zutaten",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        if (zutaten.isEmpty()) {

            Text(
                "Noch keine Zutaten hinzugefügt.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        } else {

            zutaten.forEach { zutat ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),

                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(zutat.artikel.name)

                    Text(
                        "${zutat.menge} ${zutat.artikel.einheit ?: ""}"
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),

            onClick = {
                showAddZutatSheet = true
            }
        ) {
            Text("➕ Zutat hinzufügen")
        }
    }

    if (showArtikelSheet) {
        ArtikelVerwaltungAddArtikelSheet(
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            currentArtikel = null,
            allArtikel = allArtikel,
            allKategorien = allKategorien,
            onEvent = { event ->
                onArtikelEvent(event)
                when (event) {
                    is ArtikelVerwaltungEvent.SaveArtikel -> {
                        onShowArtikelSheet(false)
                    }
                    is ArtikelVerwaltungEvent.ClearCurrentArtikel -> {
                        onShowArtikelSheet(false)
                    }
                    else -> Unit
                }
            }
        )
    }

    LaunchedEffect(allArtikel.size, showArtikelSheet) {
        if (
            !showArtikelSheet &&
            !showAddZutatSheet &&
            allArtikel.isNotEmpty()
        ) {
            showAddZutatSheet = true
        }
    }

    if (showAddZutatSheet) {

        val addZutatSheetState =
            rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )

        println("Artikel im CreateSheet: ${allArtikel.size}")
        RezeptAddZutatSheet(

            sheetState = addZutatSheetState,

            artikelListe = allArtikel,

            selectedArtikel = selectedArtikel,

            onArtikelSelected = {
                selectedArtikel = it
            },

            onConfirm = { menge ->

                selectedArtikel?.let { artikel ->

                    zutaten = zutaten + RezeptZutat(
                        artikel = artikel,
                        menge = menge
                    )

                    selectedArtikel = null
                    showAddZutatSheet = false
                }
            },

            onCreateNewArtikel = {

                // Erst Zutaten-Sheet schließen
                showAddZutatSheet = false

                // Dann Artikel-Sheet öffnen
                onShowArtikelSheet(true)

            },

            onDismiss = {
                showAddZutatSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun RezeptCreateSheetPreview() {

    val sheetState =
        androidx.compose.material3.rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    RezeptCreateSheet(
        sheetState = sheetState,
        onDismiss = {},
        onCreateConfirmed = { _, _, _, _, _, _, _-> },
        allArtikel = emptyList(),
        allKategorien = emptyList(),
        showArtikelSheet = false,
        onShowArtikelSheet = {},
        onArtikelEvent = {}
    )
}