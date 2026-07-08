package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.data.EinkaufsArtikel
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.methods.formatToDisplay
import de.thm.smartshopping.ui.composables.CustomModalSheet
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults

private enum class ActionState {
	Main,
	Exists
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkaufslisteAddArtikelSheet(
	sheetState: SheetState,
	einkaufsliste: Einkaufsliste,
	allArtikel: List<Artikel>,
	onDismiss: () -> Unit,
	onConfirmed: (artikel: EinkaufsArtikel) -> Unit,
) {
	var currentSheetMode by remember { mutableStateOf(ActionState.Main) }

	var selectedArtikel: Artikel? by remember { mutableStateOf(null) }
	var menge: Double by remember { mutableDoubleStateOf(1.0) }
	var notiz: String by remember { mutableStateOf("") }

	var mengeText: String by remember { mutableStateOf(menge.formatToDisplay()) }
	var mengeHasError by remember { mutableStateOf(false) }

	val numberFormatter = remember {
		NumberFormat.getNumberInstance(Locale.GERMAN)
	}
	// Fallback parser for dot, in case user types with a dot
	val usNumberFormatter = remember {
		NumberFormat.getNumberInstance(Locale.US)
	}

	LaunchedEffect(menge, mengeHasError) {
		if (!mengeHasError) {
			val currentParsedText = try {
				if (mengeText.isBlank()) null
				else numberFormatter.parse(mengeText)?.toDouble()
			} catch (_: Exception) {
				try {
					usNumberFormatter.parse(mengeText)?.toDouble()
				} catch (_: Exception) {
					null
				}
			}
			if (currentParsedText != menge && mengeText.isNotEmpty()) {
				mengeText = menge.formatToDisplay()
			}
		}
	}

	var expandedDropdown by remember { mutableStateOf(false) }
	var filterText by remember { mutableStateOf("") }

	val filteredArtikel = if (filterText.isBlank()) {
		allArtikel
	} else {
		allArtikel.filter {
			it.name.contains(filterText, ignoreCase = true)
		}
	}

	CustomModalSheet(
		title = "➕ Artikel hinzufügen",
		enableConfirmCancelButtons = true,
		confirmButtonName = "Speichern",
		onConfirm = { closeAction ->
			if (mengeHasError) {
				return@CustomModalSheet
			}
			if (einkaufsliste.artikel.any { it.artikel.id == selectedArtikel?.id } && currentSheetMode == ActionState.Main) {
				currentSheetMode = ActionState.Exists
			} else {
				closeAction()
			}
		},
		onConfirmAfterClose = {
			val currentSelectedArtikel = selectedArtikel
			if (currentSelectedArtikel != null && menge > 0) {
				val newEinkaufsArtikel: EinkaufsArtikel
				if (currentSheetMode == ActionState.Exists) {
					val neueMenge = menge + (einkaufsliste.artikel.firstOrNull { it.artikel.id == selectedArtikel?.id }?.menge ?: 0.0)
					newEinkaufsArtikel = EinkaufsArtikel(
						artikel = currentSelectedArtikel,
						menge = neueMenge,
						notiz = notiz.trim().takeIf { it.isNotEmpty() }
					)
				} else {
					newEinkaufsArtikel = EinkaufsArtikel(
						artikel = currentSelectedArtikel,
						menge = menge,
						notiz = notiz.trim().takeIf { it.isNotEmpty() }
					)
				}
				onConfirmed(newEinkaufsArtikel)
			}
		},
		conditionConfirmEnabled = selectedArtikel != null,
		onDismissAfterClose = {
			selectedArtikel = null
			menge = 1.0
			mengeText = menge.formatToDisplay()
			mengeHasError = false
			notiz = ""
			filterText = ""
			expandedDropdown = false
			onDismiss()
		},
		sheetState = sheetState
	) {
		AnimatedContent(
			targetState = currentSheetMode,
			modifier = Modifier
				.fillMaxWidth()
		) { mode ->
			Column(
				modifier = Modifier.fillMaxWidth()
			) {
				when (mode) {
					ActionState.Main -> {
						ExposedDropdownMenuBox(
							modifier = Modifier.fillMaxWidth(),
							expanded = expandedDropdown,
							onExpandedChange = { expandedDropdown = !expandedDropdown },
						) {
							OutlinedTextField(
								modifier = Modifier
									.menuAnchor(MenuAnchorType.PrimaryEditable)
									.fillMaxWidth(),
								value = selectedArtikel?.name ?: filterText,
								onValueChange = {
									filterText = it
									expandedDropdown = true
									selectedArtikel = null
								},
								label = { Text("Artikel auswählen oder suchen*") },
								placeholder = {
									Text("z.B. Milch")
								},
								shape = RoundedCornerShape(20.dp),
								trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
								singleLine = true
							)
							ExposedDropdownMenu(
								modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = true),
								expanded = expandedDropdown,
								onDismissRequest = { expandedDropdown = false }
							) {
								if (filteredArtikel.isEmpty()) {
									DropdownMenuItem(
										text = { Text(if (filterText.isNotBlank()) "Keine Artikel gefunden" else "Keine Artikel vorhanden") },
										onClick = {},
										enabled = false,
										colors = MenuDefaults.itemColors(

											textColor = MaterialTheme.colorScheme.onSurface

										),
									)
								} else {
									HorizontalDivider()
									filteredArtikel.take(5).forEach { artikel ->
										DropdownMenuItem(
											text = { Text(artikel.name) },
											onClick = {
												selectedArtikel = artikel
												filterText = artikel.name
												expandedDropdown = false
											},
											colors = MenuDefaults.itemColors(

												textColor = MaterialTheme.colorScheme.onSurface

											),
										)
										HorizontalDivider()
									}
								}
							}
						}

						if (selectedArtikel != null) {

							Surface(
								modifier = Modifier.fillMaxWidth(),
								shape = RoundedCornerShape(16.dp),
								color = MaterialTheme.colorScheme.secondaryContainer
							) {

								Text(
									modifier = Modifier.padding(12.dp),
									text = "✓ ${selectedArtikel!!.name}"
								)
							}
						}

							Spacer(Modifier.height(16.dp))

						OutlinedTextField(
							shape = RoundedCornerShape(20.dp),
							placeholder = {
								Text("1")
							},
							modifier = Modifier.fillMaxWidth(),
							label = { Text("Menge") },
							value = mengeText,
							onValueChange = { newText ->
								mengeText = newText

								if (newText.isEmpty()) {
									mengeHasError = true
								} else if (newText == "," || newText == ".") {
									mengeHasError = true
								} else {
									val parsedDouble = try {
										numberFormatter.parse(newText)?.toDouble()
									} catch (_: Exception) {
										try {
											usNumberFormatter.parse(newText)?.toDouble()
										} catch (_: Exception) {
											null
										}
									}

									if (parsedDouble != null && parsedDouble >= 0) {
										menge = parsedDouble
										mengeHasError = false
									} else {
										mengeHasError = true
									}
								}
							},
							isError = mengeHasError,
							supportingText = {
								if (mengeHasError && mengeText.isNotEmpty()) {
									Text("Ungültige Zahl")
								} else if(mengeHasError && mengeText.isEmpty()) {
									Text("Menge erforderlich")
								}
							},
							singleLine = true,
							keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
						)

						Spacer(Modifier.height(16.dp))

						OutlinedTextField(
							modifier = Modifier.fillMaxWidth(),
							value = notiz,
							onValueChange = { notiz = it },
							label = { Text("Notiz (Optional)") },
							placeholder = {
								Text("z.B fettarme Milch")
							},
							shape = RoundedCornerShape(20.dp),
							keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
							minLines = 2
						)
					}

					ActionState.Exists -> {
						val neueMenge = menge + (einkaufsliste.artikel.firstOrNull {
							it.artikel.id == selectedArtikel?.id
						}?.menge ?: 0.0)

						Column {

							Text(
								text = "⚠️ Artikel bereits vorhanden",
								style = MaterialTheme.typography.titleMedium
							)

							Spacer(Modifier.height(12.dp))

							Text(
								text = "${selectedArtikel?.name} befindet sich bereits in dieser Einkaufsliste."
							)

							Spacer(Modifier.height(12.dp))

							Text(
								text = "Neue Menge nach dem Zusammenführen:"
							)

							Spacer(Modifier.height(8.dp))

							Surface(
								shape = RoundedCornerShape(12.dp),
								color = MaterialTheme.colorScheme.secondaryContainer
							) {
								Text(
									modifier = Modifier.padding(
										horizontal = 12.dp,
										vertical = 8.dp
									),
									text = "${neueMenge.formatToDisplay()} ${selectedArtikel?.einheit ?: ""}"
								)
							}
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EinkaufslisteAddArtikelSheetPreview() {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val sampleArtikels = listOf(
		Artikel(id = "1", name = "Apfel"),
		Artikel(id = "2", name = "Banane"),
		Artikel(id = "3", name = "Milch (1.5% Fett)"),
		Artikel(id = "4", name = "Vollkornbrot"),
		Artikel(id = "5", name = "Hähnchenbrustfilet")
	)

	SmartShoppingTheme {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
			EinkaufslisteAddArtikelSheet(
				sheetState = sheetState,
				einkaufsliste = Einkaufsliste(id = "1", name = "Test Einkaufsliste"),
				allArtikel = sampleArtikels,
				onDismiss = { println("Preview Dismiss") },
				onConfirmed = { einkaufsArtikel -> println("Preview Artikel Confirmed: ${einkaufsArtikel.artikel.name}, Menge: ${einkaufsArtikel.menge}") },
			)
		}
	}
}