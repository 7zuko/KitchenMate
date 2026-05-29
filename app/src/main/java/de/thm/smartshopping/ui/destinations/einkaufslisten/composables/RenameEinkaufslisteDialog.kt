package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import de.thm.smartshopping.data.Einkaufsliste

@Composable
fun RenameEinkaufslisteDialog(
	einkaufsliste: Einkaufsliste,
	onConfirm: (newName: String) -> Unit,
	onDismiss: () -> Unit,
) {
	var newName by remember(einkaufsliste.id) { mutableStateOf(einkaufsliste.name) }

	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("Einkaufsliste umbenennen") },
		text = {
			OutlinedTextField(
				value = newName,
				onValueChange = { newName = it },
				label = { Text("Neuer Name") },
				singleLine = true,
				keyboardOptions = KeyboardOptions.Default.copy(
					capitalization = KeyboardCapitalization.Sentences
				)
			)
		},
		confirmButton = {
			TextButton(
				onClick = {
					if (newName.isNotBlank()) {
						onConfirm(newName)
					}
				}
			) {
				Text("Speichern")
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismiss
			) {
				Text("Abbrechen")
			}
		},
	)
}

@Preview(showBackground = true)
@Composable
private fun RenameEinkaufslisteDialogPreview() {
	DeleteEinkaufslisteDialog(
		einkaufsliste = Einkaufsliste("1", "Einkaufsliste 1"),
		onConfirm = {},
		onDismiss = {}
	)
}