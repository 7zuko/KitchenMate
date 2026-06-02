package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.thm.smartshopping.data.Einkaufsliste
import androidx.compose.material3.MaterialTheme

@Composable
fun DeleteEinkaufslisteDialog(
	einkaufsliste: Einkaufsliste,
	onConfirm: () -> Unit,
	onDismiss: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		containerColor = MaterialTheme.colorScheme.surface,
		title = {
			Text(
				text = "🗑️ Einkaufsliste löschen"
			)
		},
		text = {
			Text(
				"Die Liste \"${einkaufsliste.name}\" wird dauerhaft gelöscht. Diese Aktion kann nicht rückgängig gemacht werden."
			)
		},
		confirmButton = {
			TextButton(
				onClick = onConfirm
			) {
				Text(
					text = "Löschen",
					color = MaterialTheme.colorScheme.error
				)
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
private fun DeleteEinkaufslisteDialogPreview() {
	DeleteEinkaufslisteDialog(
		einkaufsliste = Einkaufsliste("1", "Einkaufsliste 1"),
		onConfirm = {},
		onDismiss = {}
	)
}