package de.thm.smartshopping.vorrat

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.thm.smartshopping.ui.theme.SmartShoppingTheme

@Composable
fun ArtikelNeuDialog(
	state: String = "",
	onEvent: (String) -> Unit = {},
	modifier: Modifier = Modifier
) {
	AlertDialog(
		modifier = modifier,
		onDismissRequest = {
			onEvent("TODO")
		},
		title = {
			Text(text = "Artikel erstellen")
		},
		text = {
			TextField(
				value = state,
				onValueChange = {
					onEvent("TODO")
				},
				placeholder = {
					Text(text = "Artikelname")
				}
			)
		},
		confirmButton = {
			Button(
				onClick = {
					onEvent("TODO")
				}
			) {
				Text(text = "Speichern")
			}
		}
	)
}

@Preview(showBackground = true)
@Composable
fun ArtikelNeuDialogPreview() {
	SmartShoppingTheme {
		ArtikelNeuDialog()
	}
}