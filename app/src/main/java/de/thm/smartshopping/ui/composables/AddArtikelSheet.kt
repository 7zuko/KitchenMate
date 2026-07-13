package de.thm.smartshopping.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.ArtikelKategorie
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import de.thm.smartshopping.ui.theme.defaultOutlinedTextFieldColors
import java.util.UUID

private enum class ActionState {
    Main,
    Exists,
    NewKategorie
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArtikelSheet(
    sheetState: SheetState,
    currentArtikel: Artikel?,
    allArtikel: List<Artikel>,
    allKategorien: List<ArtikelKategorie>,
    onSaveArtikel: (Artikel) -> Unit,
    onSaveKategorie: (ArtikelKategorie) -> Unit,
    onDismiss: () -> Unit
) {
    var showEmojiPicker by remember {
        mutableStateOf(false)
    }
    var currentSheetMode by remember { mutableStateOf(ActionState.Main) }

    var name: String by remember { mutableStateOf("") }
    var einheit: String by remember { mutableStateOf("") }
    val standardEinheiten = listOf(
        "Stück",
        "Gramm",
        "Kilogramm",
        "Milliliter",
        "Liter",
        "Packung",
        "Sack",
        "Dose",
        "Flasche",
        "Kasten",
        "Beutel",
        "Rolle",
        "Glas",
        "Eigene Einheit..."
    )

    var expandedEinheit by remember {
        mutableStateOf(false)
    }

    var eigeneEinheit by remember {
        mutableStateOf(false)
    }
    var emoji: String by remember { mutableStateOf("🛒") }
    var selectedKategorie: ArtikelKategorie? by remember { mutableStateOf(null) }

    var expandedDropdown by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }

    val filteredKategorie = if (filterText.isBlank()) {
        allKategorien
    } else {
        allKategorien.filter {
            it.name.contains(filterText, ignoreCase = true)
        }
    }

    LaunchedEffect(currentArtikel) {
        if (currentArtikel != null) {
            name = currentArtikel.name
            einheit = currentArtikel.einheit ?: ""
            emoji = currentArtikel.emoji
            selectedKategorie = currentArtikel.kategorie
            filterText = currentArtikel.kategorie?.name ?: ""
        }
    }
    var newKategorieName: String by remember { mutableStateOf("") }

    CustomModalSheet(
        title = when {
            currentSheetMode == ActionState.NewKategorie ->
                "Neue Kategorie"

            currentArtikel != null ->
                "Artikel bearbeiten"

            else ->
                "Neuer Artikel"
        },
        enableConfirmCancelButtons = true,
        confirmButtonName =
            if (currentArtikel != null)
                "Änderungen speichern"
            else
                "Speichern",
        onConfirm = { closeAction ->
            if (allArtikel.any { it.name.equals(name, true) } && currentSheetMode == ActionState.Main) {
                currentSheetMode = ActionState.Exists

            } else if (currentSheetMode == ActionState.NewKategorie) {
                val currentNewKategorieName = newKategorieName.trim()
                if (currentNewKategorieName.isNotBlank()) {
                    val newKategorie = ArtikelKategorie(
                        id = UUID.randomUUID().toString(),
                        name = currentNewKategorieName
                    )
                    onSaveKategorie(newKategorie)
                    selectedKategorie = newKategorie
                    currentSheetMode = ActionState.Main
                    newKategorieName = ""
                }

            } else {
                closeAction()
            }
        },
        onConfirmAfterClose = {
            val currentName = name.trim()

            if (currentName.isNotBlank()) {

                val artikel = Artikel(
                    id = currentArtikel?.id ?: UUID.randomUUID().toString(),
                    name = currentName,
                    kategorie = selectedKategorie,
                    einheit = einheit.ifBlank { null },
                    emoji = emoji
                )

                onSaveArtikel(artikel)
            }
        },
        conditionConfirmEnabled = if (currentSheetMode == ActionState.NewKategorie) {
            newKategorieName.trim().isNotBlank()
        } else {
            name.trim().isNotBlank()
        },
        onDismiss = { closeAction ->
            if (currentSheetMode == ActionState.Exists) {
                currentSheetMode = ActionState.Main
            } else {
                closeAction()
            }
        },
        onDismissAfterClose = {
            name = ""
            einheit = ""
            emoji = "🛒"
            selectedKategorie = null
            filterText = ""
            expandedDropdown = false
            newKategorieName = ""
            onDismiss()
        },
        sheetState = sheetState
    ) {
        AnimatedContent(
            targetState = currentSheetMode,
            modifier = Modifier
                .fillMaxWidth()
        ) { mode ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                when (mode) {
                    ActionState.Main -> {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            colors = defaultOutlinedTextFieldColors()
                        )

                        Spacer(Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedEinheit,
                            onExpandedChange = {
                                expandedEinheit = !expandedEinheit
                            }
                        ) {

                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),

                                value = einheit,

                                onValueChange = {},

                                readOnly = true,

                                label = {
                                    Text("Mengeneinheit (optional)")
                                },

                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedEinheit
                                    )
                                },

                                shape = RoundedCornerShape(20.dp),

                                colors = defaultOutlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedEinheit,
                                onDismissRequest = {
                                    expandedEinheit = false
                                }
                            ) {

                                standardEinheiten.forEach {

                                    DropdownMenuItem(

                                        text = {
                                            Text(it)
                                        },

                                        colors = MenuDefaults.itemColors(
                                            textColor = MaterialTheme.colorScheme.onSurface
                                        ),

                                        onClick = {

                                            expandedEinheit = false

                                            if (it == "Eigene Einheit...") {

                                                eigeneEinheit = true
                                                einheit = ""

                                            } else {

                                                eigeneEinheit = false
                                                einheit = it
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        if (eigeneEinheit) {

                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),

                                value = einheit,

                                onValueChange = {
                                    einheit = it
                                },

                                label = {
                                    Text("Eigene Einheit")
                                },

                                singleLine = true,

                                shape = RoundedCornerShape(20.dp),

                                colors = defaultOutlinedTextFieldColors()
                            )
                        }

                        Spacer(Modifier.height(12.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showEmojiPicker = true
                                }
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 14.dp),

                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "Emoji",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = emoji,
                                    fontSize = 30.sp
                                )

                                Spacer(Modifier.width(12.dp))

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExposedDropdownMenuBox(
                                modifier = Modifier.weight(1f),
                                expanded = expandedDropdown,
                                onExpandedChange = { expandedDropdown = !expandedDropdown },
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryEditable),
                                    value = selectedKategorie?.name ?: filterText,
                                    onValueChange = {
                                        filterText = it
                                        expandedDropdown = true
                                        selectedKategorie = null
                                    },
                                    label = { Text("Kategorie auswählen*") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(20.dp),
                                    colors = defaultOutlinedTextFieldColors()
                                )
                                ExposedDropdownMenu(
                                    modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = true),
                                    expanded = expandedDropdown,
                                    onDismissRequest = { expandedDropdown = false },
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    if (filteredKategorie.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text(if (filterText.isNotBlank()) "Keine Kategorie gefunden" else "Keine Kategorien vorhanden") },
                                            onClick = {},
                                            enabled = false
                                        )
                                    } else {
                                        HorizontalDivider()
                                        filteredKategorie.forEach { kategorie ->
                                            DropdownMenuItem(
                                                text = { Text(kategorie.name,
                                                    color = MaterialTheme.colorScheme.onSurface) },
                                                onClick = {
                                                    selectedKategorie = kategorie
                                                    filterText = kategorie.name
                                                    expandedDropdown = false
                                                }
                                            )
                                            HorizontalDivider()
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                modifier = Modifier
                                    .padding(top = 8.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                onClick = { currentSheetMode = ActionState.NewKategorie }
                            ) {
                                Icon(Icons.Default.Add, "Kategorie hinzufügen")
                            }
                        }
                    }
                    ActionState.Exists -> {
                        Text("Ein Artikel mit dem Namen \"$name\" ist bereits vorhanden.")
                        Spacer(Modifier.height(8.dp))
                        Text("Soll trotzdem ein neuer Artikel angelegt werden?")
                    }

                    ActionState.NewKategorie -> {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = newKategorieName,
                            onValueChange = { newKategorieName = it },
                            label = { Text("Name") },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            colors = defaultOutlinedTextFieldColors()
                        )
                    }
                }
            }
        }
    }
    if (showEmojiPicker) {

        EmojiPicker(
            onEmojiSelected = {
                emoji = it
                showEmojiPicker = false
            },

            onDismiss = {
                showEmojiPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddArtikelSheetPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sampleKategorien = listOf(
        ArtikelKategorie(id = "1", name = "Milchprodukte"),
        ArtikelKategorie(id = "2", name = "Backwaren"),
        ArtikelKategorie(id = "3", name = "Getränke")
    )

    SmartShoppingTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            AddArtikelSheet(
                sheetState = sheetState,
                currentArtikel = null,
                allArtikel = emptyList(),
                allKategorien = sampleKategorien,
                onSaveArtikel = {},
                onSaveKategorie = {},
                onDismiss = {}
            )
        }
    }
}