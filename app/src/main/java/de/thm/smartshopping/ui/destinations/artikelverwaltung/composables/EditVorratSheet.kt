package de.thm.smartshopping.ui.destinations.artikelverwaltung.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.ui.composables.CustomModalSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVorratSheet(
    sheetState: SheetState,
    artikel: Artikel,
    aktuelleMenge: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {

    var menge by remember {

        mutableStateOf(
            aktuelleMenge.toString()
        )
    }

    CustomModalSheet(

        title = "Bestand bearbeiten",

        sheetState = sheetState,

        conditionConfirmEnabled =
            menge.toDoubleOrNull() != null,

        onDismissAfterClose = onDismiss,

        onConfirmAfterClose = {
            onSave(
                menge.toDouble()
            )
        }
    ) {

        Text(
            "${artikel.emoji} ${artikel.name}"
        )

        OutlinedTextField(

            modifier = Modifier.fillMaxWidth(),

            value = menge,

            onValueChange = {
                menge = it
            },

            label = {
                Text("Bestand")
            },

            suffix = {
                Text(
                    artikel.einheit ?: ""
                )
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }
}