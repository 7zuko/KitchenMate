package de.thm.smartshopping.ui.destinations.rezepte.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptCreateSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onCreateConfirmed: (
        name: String,
        beschreibung: String?,
        zubereitungszeit: Int
    ) -> Unit
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

    CustomModalSheet(
        title = "Neues Rezept",
        confirmButtonName = "Speichern",
        sheetState = sheetState,

        conditionConfirmEnabled = name.isNotBlank(),

        onConfirmAfterClose = {
            onCreateConfirmed(
                name.trim(),
                beschreibung.trim().ifBlank { null },
                zubereitungszeit.toIntOrNull() ?: 0
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
            onClick = {}
        ) {
            Text("📷 Bild auswählen")
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
        onCreateConfirmed = { _, _, _ -> }
    )
}