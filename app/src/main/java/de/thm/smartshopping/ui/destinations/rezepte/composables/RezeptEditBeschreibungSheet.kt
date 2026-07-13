package de.thm.smartshopping.ui.destinations.rezepte.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import de.thm.smartshopping.data.Rezept
import de.thm.smartshopping.ui.composables.CustomModalSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptEditBeschreibungSheet(
    sheetState: SheetState,
    rezept: Rezept,
    onDismiss: () -> Unit,
    onSave: (String?) -> Unit
) {

    var beschreibung by remember {

        mutableStateOf(
            rezept.beschreibung ?: ""
        )

    }

    CustomModalSheet(

        title = "Beschreibung bearbeiten",

        confirmButtonName = "Speichern",

        sheetState = sheetState,

        onConfirmAfterClose = {

            onSave(
                beschreibung.trim()
                    .ifBlank { null }
            )

        },

        onDismissAfterClose = onDismiss

    ) {

        OutlinedTextField(

            value = beschreibung,

            onValueChange = {

                beschreibung = it

            },

            modifier = Modifier.fillMaxWidth(),

            minLines = 6,

            keyboardOptions = KeyboardOptions(

                capitalization =
                    KeyboardCapitalization.Sentences

            )

        )

    }

}