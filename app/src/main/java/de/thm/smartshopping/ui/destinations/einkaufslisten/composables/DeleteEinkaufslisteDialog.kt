package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.thm.smartshopping.data.Einkaufsliste

@Composable
fun DeleteEinkaufslisteDialog(
	einkaufsliste: Einkaufsliste,
	onConfirm: () -> Unit,
	onDismiss: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("Einkaufsliste löschen") },
		text = { Text("Möchtest du die Einkaufsliste \"${einkaufsliste.name}\" wirklich löschen?") },
		confirmButton = {
			TextButton(
				onClick = onConfirm
			) {
				Text("Löschen")
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