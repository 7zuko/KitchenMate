package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.composables.CustomModalSheet
import de.thm.smartshopping.ui.theme.SmartShoppingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkaufslisteCreateSheet(
	sheetState: SheetState,
	onDismiss: () -> Unit,
	onCreateConfirmed: (name: String) -> Unit,
) {
	var listName by remember { mutableStateOf("") }

	CustomModalSheet(
		title = "Neue Einkaufsliste",
		enableConfirmCancelButtons = true,
		confirmButtonName = "Speichern",
		onConfirmAfterClose = {
			val trimmedName = listName.trim()
			if (trimmedName.isNotBlank()) {
				onCreateConfirmed(trimmedName)
			}
		},
		conditionConfirmEnabled = listName.isNotEmpty(),
		onDismissAfterClose = {
			listName = ""
			onDismiss()
		},
		sheetState = sheetState
	) {
		OutlinedTextField(
			value = listName,
			onValueChange = { listName = it },
			label = { Text("Name der Einkaufsliste") },
			singleLine = true,
			keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 8.dp)
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CreateEinkaufslisteSheetPreview() {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

	SmartShoppingTheme {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
			EinkaufslisteCreateSheet(
				sheetState = sheetState,
				onDismiss = { println("Preview Dismiss") },
				onCreateConfirmed = { name -> println("Preview Create: $name") }
			)
		}
	}
}