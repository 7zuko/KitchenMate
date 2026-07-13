package de.thm.smartshopping.ui.destinations.rezepte.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.RezeptZutat
import de.thm.smartshopping.data.ZutatenStatus
import de.thm.smartshopping.ui.composables.CustomModalSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptZutatenCard(
    zutaten: List<RezeptZutat>,
    zutatenStatus: Map<String, ZutatenStatus>,
    onAddClick: () -> Unit,
    onDeleteClick: (RezeptZutat) -> Unit,
    onUpdateMenge: (RezeptZutat, Double) -> Unit,
    onAddToShoppingList: (RezeptZutat) -> Unit
) {
    var bearbeiteteZutat by remember {
        mutableStateOf<RezeptZutat?>(null)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {

        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Zutaten (${zutaten.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                FilledIconButton(
                    onClick = onAddClick
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Zutat hinzufügen"
                    )
                }
            }

            HorizontalDivider()

            if (zutaten.isEmpty()) {

                Text(
                    text = "Noch keine Zutaten vorhanden",
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

            } else {

                zutaten.forEachIndexed { index, zutat ->

                    RezeptZutatRow(
                        zutat = zutat,
                        status = zutatenStatus[zutat.artikel.id]
                            ?: ZutatenStatus.FEHLT,
                        onDeleteClick = {
                            onDeleteClick(zutat)
                        },
                        onEditClick = {

                            bearbeiteteZutat = zutat

                        },
                        onShoppingListClick = {
                            onAddToShoppingList(zutat)
                        }
                    )

                    if (index != zutaten.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }
    }

    if (bearbeiteteZutat != null) {

        var neueMenge by remember(bearbeiteteZutat) {
            mutableStateOf(bearbeiteteZutat!!.menge.toString())
        }

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        CustomModalSheet(
            sheetState = sheetState,
            title = "Menge ändern",

            conditionConfirmEnabled =
                neueMenge.toDoubleOrNull() != null,

            onConfirmAfterClose = {

                onUpdateMenge(
                    bearbeiteteZutat!!,
                    neueMenge.toDouble()
                )

                bearbeiteteZutat = null
            },

            onDismissAfterClose = {
                bearbeiteteZutat = null
            }
        ) {

            OutlinedTextField(
                value = neueMenge,

                onValueChange = {
                    neueMenge = it
                },

                label = {
                    Text("Neue Menge")
                },

                singleLine = true
            )
        }
    }
}

@Composable
private fun RezeptZutatRow(
    zutat: RezeptZutat,
    status: ZutatenStatus,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onShoppingListClick: () -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            shape = RoundedCornerShape(12.dp),
            color =
                when (status) {

                    ZutatenStatus.VORHANDEN ->
                        MaterialTheme.colorScheme.primaryContainer

                    ZutatenStatus.TEILWEISE ->
                        MaterialTheme.colorScheme.tertiaryContainer

                    ZutatenStatus.FEHLT ->
                        MaterialTheme.colorScheme.errorContainer
                }
        ) {

            Text(
                text = zutat.artikel.emoji,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = zutat.artikel.name,
                style = MaterialTheme.typography.titleMedium,
                color =
                    when (status) {

                        ZutatenStatus.VORHANDEN ->
                            MaterialTheme.colorScheme.onSurface

                        ZutatenStatus.TEILWEISE ->
                            MaterialTheme.colorScheme.tertiary

                        ZutatenStatus.FEHLT ->
                            MaterialTheme.colorScheme.error
                    }
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "${zutat.menge} ${zutat.artikel.einheit ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {

            IconButton(
                onClick = {
                    expanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
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
                    text = {
                        Text("Menge ändern")
                    },
                    onClick = {
                        expanded = false
                        onEditClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Auf Einkaufsliste setzen")
                    },
                    onClick = {
                        expanded = false
                        onShoppingListClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Entfernen")
                    },
                    onClick = {
                        expanded = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}