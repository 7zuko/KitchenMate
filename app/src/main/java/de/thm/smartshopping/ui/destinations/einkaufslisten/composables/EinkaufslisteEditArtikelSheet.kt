package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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
import de.thm.smartshopping.methods.formatToDisplay
import de.thm.smartshopping.ui.composables.CustomModalSheet
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkaufslisteEditArtikelSheet(
	sheetState: SheetState,
	einkaufsArtikel: EinkaufsArtikel,
	onDismiss: () -> Unit,
	onConfirmed: (artikel: EinkaufsArtikel) -> Unit,
) {
	var menge: Double by remember(einkaufsArtikel.menge) { mutableDoubleStateOf(einkaufsArtikel.menge) }
	var notiz: String by remember(einkaufsArtikel.notiz) { mutableStateOf(einkaufsArtikel.notiz ?: "") }

	var mengeText: String by remember { mutableStateOf(menge.formatToDisplay()) }
	var mengeHasError by remember { mutableStateOf(false) }

	val numberFormatter = remember {
		NumberFormat.getNumberInstance(Locale.GERMAN)
	}
	// Fallback parser for dot, in case user types with a dot
	val usNumberFormatter = remember {
		NumberFormat.getNumberInstance(Locale.US)
	}

	LaunchedEffect(menge) {
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

	CustomModalSheet(
		title = "Eintrag anpassen",
		enableConfirmCancelButtons = true,
		confirmButtonName = "Speichern",
		onConfirm = { closeAction ->
			if (mengeHasError) {
				return@CustomModalSheet
			} else {
				closeAction()
			}
		},
		onConfirmAfterClose = {
			val updatedEinkaufsArtikel = einkaufsArtikel.copy(menge = menge, notiz = notiz)
			onConfirmed(updatedEinkaufsArtikel)
		},
		onDismissAfterClose = {
			menge = 1.0
			mengeText = menge.formatToDisplay()
			mengeHasError = false
			notiz = ""
			onDismiss()
		},
		sheetState = sheetState
	) {
		Column(
			modifier = Modifier.fillMaxWidth()
		) {
			OutlinedTextField(
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

			Spacer(Modifier.height(12.dp))

			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				value = notiz,
				onValueChange = { notiz = it },
				label = { Text("Notiz (Optional)") },
				keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
				minLines = 2
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EinkaufslisteEditArtikelSheetPreview() {
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
			EinkaufslisteEditArtikelSheet(
				sheetState = sheetState,
				einkaufsArtikel = EinkaufsArtikel(artikel = sampleArtikels[0], menge = 2.0, notiz = "Test"),
				onDismiss = { println("Preview Dismiss") },
				onConfirmed = { einkaufsArtikel -> println("Preview Artikel Confirmed: ${einkaufsArtikel.artikel.name}, Menge: ${einkaufsArtikel.menge}") },
			)
		}
	}
}