package de.thm.smartshopping.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalSheet(
	sheetState: SheetState,
	title: String,
	enableConfirmCancelButtons: Boolean = true,
	confirmButtonName: String = "Speichern",
	onConfirm: (closeAction: () -> Unit) -> Unit = { closeAction -> closeAction() },
	onConfirmAfterClose: () -> Unit = {},
	conditionConfirmEnabled: Boolean = true,
	onDismiss: (closeAction: () -> Unit) -> Unit = { closeAction -> closeAction() },
	onDismissAfterClose: () -> Unit = {},
	content: @Composable (ColumnScope.() -> Unit),
) {
	val scope = rememberCoroutineScope()

	ModalBottomSheet(
		sheetState = sheetState,
		onDismissRequest = onDismissAfterClose
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.imePadding()
				.padding(
					bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
				)
				.padding(horizontal = 16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier.padding(bottom = 16.dp)
			)

			content()

			if (enableConfirmCancelButtons) {
				Spacer(Modifier.height(20.dp))

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(
						onClick = {
							onDismiss {
								scope.launch { sheetState.hide() }.invokeOnCompletion {
									if (!sheetState.isVisible) {
										onDismissAfterClose()
									}
								}
							}
						}
					) {
						Text("Abbrechen")
					}
					Spacer(Modifier.width(16.dp))

					Button(
						onClick = {
							onConfirm {
								scope.launch { sheetState.hide() }.invokeOnCompletion {
									if (!sheetState.isVisible) {
										onConfirmAfterClose()
										onDismissAfterClose()
									}
								}
							}
						},
						enabled = conditionConfirmEnabled
					) {
						Text(confirmButtonName)
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CreateEinkaufslisteSheetPreview() {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	sheetState

	SmartShoppingTheme {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
			CustomModalSheet(
				title = "Test",
				enableConfirmCancelButtons = true,
				onConfirm = {},
				conditionConfirmEnabled = true,
				onDismiss = {},
				sheetState = sheetState
			) {
				Text("Content")
			}
		}
	}
}