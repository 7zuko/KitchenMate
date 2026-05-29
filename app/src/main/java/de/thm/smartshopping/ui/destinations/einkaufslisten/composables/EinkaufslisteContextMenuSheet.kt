package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import kotlinx.coroutines.launch

private enum class SheetMode {
	ActionList,
	Rename,
	Delete
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkaufslisteContextMenuSheet(
	einkaufsliste: Einkaufsliste,
	sheetState: SheetState,
	onRenameConfirmed: (einkaufslistee: Einkaufsliste, newName: String) -> Unit,
	onDeleteConfirmed: (einkaufslistee: Einkaufsliste) -> Unit,
	onDismiss: () -> Unit,
) {
	val scope = rememberCoroutineScope()
	var currentSheetMode by remember { mutableStateOf(SheetMode.ActionList) }
	var renameInputText by remember(einkaufsliste.name) { mutableStateOf(einkaufsliste.name) }
	val focusManager = LocalFocusManager.current

	LaunchedEffect(sheetState.isVisible) {
		if (!sheetState.isVisible) {
			currentSheetMode = SheetMode.ActionList
			renameInputText = einkaufsliste.name
		}
	}

	LaunchedEffect(einkaufsliste.id) {
		currentSheetMode = SheetMode.ActionList
		renameInputText = einkaufsliste.name
	}

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
		containerColor = MaterialTheme.colorScheme.surface
	) {
		AnimatedContent(
			targetState = currentSheetMode,
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
				)
				.imePadding()
		) { mode ->
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp)
			) {
				when (mode) {
					SheetMode.ActionList -> {
						ActionListContent(
							einkaufslisteName = einkaufsliste.name,
							onRenameClick = { currentSheetMode = SheetMode.Rename },
							onDeleteClick = { currentSheetMode = SheetMode.Delete }
						)
					}
					SheetMode.Rename -> {
						RenameContent(
							currentName = renameInputText,
							onNameChange = { renameInputText = it },
							onConfirm = {
								val newName = renameInputText.trim()
								if (newName.isNotBlank() && newName != einkaufsliste.name) {
									focusManager.clearFocus()
									scope.launch { sheetState.hide() }.invokeOnCompletion {
										if (!sheetState.isVisible) {
											onRenameConfirmed(einkaufsliste, newName)
										}
									}
								} else if (newName.isBlank()) {

								} else {
									scope.launch { sheetState.hide() }.invokeOnCompletion {
										if (!sheetState.isVisible) onDismiss()
									}
								}
							},
							onCancel = {
								currentSheetMode = SheetMode.ActionList
								renameInputText = einkaufsliste.name
							}
						)
					}
					SheetMode.Delete -> {
						DeleteContent(
							einkaufslisteName = einkaufsliste.name,
							onConfirm = {
								scope.launch { sheetState.hide() }.invokeOnCompletion {
									if (!sheetState.isVisible) {
										onDeleteConfirmed(einkaufsliste)
									}
								}
							},
							onCancel = { currentSheetMode = SheetMode.ActionList }
						)
					}
				}
				Spacer(modifier = Modifier.height(16.dp))
			}
		}
	}
}

@Composable
private fun ActionListContent(
	einkaufslisteName: String,
	onRenameClick: () -> Unit,
	onDeleteClick: () -> Unit,
) {
	Text(
		text = einkaufslisteName,
		style = MaterialTheme.typography.titleLarge,
		textAlign = TextAlign.Center,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 16.dp)
	)
	HorizontalDivider()
	ListItem(
		headlineContent = { Text("Umbenennen") },
		leadingContent = { Icon(Icons.Default.Edit, "Umbenennen") },
		modifier = Modifier.clickable(onClick = onRenameClick)
	)
	ListItem(
		headlineContent = { Text("Löschen") },
		leadingContent = { Icon(Icons.Default.Delete, "Löschen") },
		modifier = Modifier.clickable(onClick = onDeleteClick)
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Context Menu - Action List")
@Composable
fun EinkaufslisteContextMenuSheet_ActionList_Preview() {
	SmartShoppingTheme {
		Column(modifier = Modifier
			.padding(16.dp).fillMaxWidth()
			//.background(MaterialTheme.colorScheme.surface)
		) {
			ActionListContent(einkaufslisteName = "Liste Grillparty", onRenameClick = {}, onDeleteClick = {})
		}
	}
}

@Composable
private fun RenameContent(
	currentName: String,
	onNameChange: (String) -> Unit,
	onConfirm: () -> Unit,
	onCancel: () -> Unit,
) {
	val focusManager = LocalFocusManager.current
	Text(
		text = "Einkaufsliste umbenennen",
		style = MaterialTheme.typography.titleMedium,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 16.dp),
		textAlign = TextAlign.Center
	)
	OutlinedTextField(
		value = currentName,
		onValueChange = onNameChange,
		label = { Text("Neuer Name") },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			capitalization = KeyboardCapitalization.Sentences,
			imeAction = ImeAction.Done
		),
		keyboardActions = KeyboardActions(onDone = {
			focusManager.clearFocus()
			onConfirm()
		}),
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp)
	)
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 8.dp),
		horizontalArrangement = Arrangement.End
	) {
		TextButton(onClick = onCancel) {
			Text("Abbrechen")
		}
		Spacer(Modifier.width(8.dp))
		Button(onClick = onConfirm, enabled = currentName.trim().isNotBlank()) {
			Text("Speichern")
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Context Menu - Rename Mode")
@Composable
fun EinkaufslisteContextMenuSheet_Rename_Preview() {
	val sampleListe = Einkaufsliste(id = "1", name = "Preview Liste Grillparty")

	SmartShoppingTheme {
		Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
			RenameContent(currentName = "Preview Liste Grillparty", onNameChange = {}, onConfirm = {}, onCancel = {})
		}
	}
}

@Composable
private fun DeleteContent(
	einkaufslisteName: String,
	onConfirm: () -> Unit,
	onCancel: () -> Unit,
) {
	Text(
		text = "Löschen bestätigen",
		style = MaterialTheme.typography.titleMedium,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 16.dp),
		textAlign = TextAlign.Center
	)
	Text(
		text = "Möchtest du die Einkaufsliste \"$einkaufslisteName\" wirklich löschen?",
		textAlign = TextAlign.Center,
		style = MaterialTheme.typography.bodyMedium,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 16.dp)
	)
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 16.dp),
		horizontalArrangement = Arrangement.End
	) {
		TextButton(onClick = onCancel) {
			Text("Abbrechen")
		}
		Spacer(Modifier.width(16.dp))
		Button(
			onClick = onConfirm,
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
		) {
			Text("Löschen")
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Context Menu - Delete Mode")
@Composable
fun EinkaufslisteContextMenuSheet_ConfirmDelete_Preview() {
	val sampleListe = Einkaufsliste(id = "1", name = "Alte Aufgaben")
	SmartShoppingTheme {
		Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
			DeleteContent(einkaufslisteName = "Alte Aufgaben", onConfirm = {}, onCancel = {})
		}
	}
}