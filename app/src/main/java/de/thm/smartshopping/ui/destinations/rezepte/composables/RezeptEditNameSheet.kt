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
fun RezeptEditNameSheet(
    sheetState: SheetState,
    rezept: Rezept,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {

    var name by remember(rezept.name) {
        mutableStateOf(rezept.name)
    }

    CustomModalSheet(

        sheetState = sheetState,

        title = "Rezept umbenennen",

        confirmButtonName = "Speichern",

        conditionConfirmEnabled =
            name.isNotBlank() &&
                    name.trim() != rezept.name,

        onConfirmAfterClose = {

            onSave(
                name.trim()
            )

        },

        onDismissAfterClose = onDismiss

    ) {

        OutlinedTextField(

            value = name,

            onValueChange = {
                name = it
            },

            modifier = Modifier.fillMaxWidth(),

            label = {
                androidx.compose.material3.Text("Rezeptname")
            },

            singleLine = true,

            keyboardOptions = KeyboardOptions(
                capitalization =
                    KeyboardCapitalization.Sentences
            )
        )
    }
}