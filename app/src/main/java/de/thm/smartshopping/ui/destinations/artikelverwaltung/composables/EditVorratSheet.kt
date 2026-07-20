package de.thm.smartshopping.ui.destinations.artikelverwaltung.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import de.thm.smartshopping.data.Artikel
import de.thm.smartshopping.ui.composables.CustomModalSheet
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.theme.defaultOutlinedTextFieldColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVorratSheet(
    sheetState: SheetState,
    artikel: Artikel,
    aktuelleMenge: Double,
    aktuellesMhd: Long?,
    onDismiss: () -> Unit,
    onSave: (Double, Long?) -> Unit
) {

    var menge by remember {

        mutableStateOf(
            aktuelleMenge.toString()
        )
    }

    var mindesthaltbarBis by remember {
        mutableStateOf(aktuellesMhd)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = aktuellesMhd
    )

    CustomModalSheet(

        title = "Bestand bearbeiten",

        sheetState = sheetState,

        conditionConfirmEnabled =
            menge.toDoubleOrNull() != null,

        onDismissAfterClose = onDismiss,

        onConfirmAfterClose = {
            onSave(
                menge.toDouble(),
                mindesthaltbarBis
            )
        }
    ) {

        Text(
            "${artikel.emoji} ${artikel.name}"
        )

        OutlinedTextField(

            modifier = Modifier.fillMaxWidth(),

            value = menge,

            onValueChange = {
                menge = it
            },

            label = {
                Text("Bestand")
            },

            suffix = {
                Text(
                    artikel.einheit ?: ""
                )
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                showDatePicker = true
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                if (mindesthaltbarBis == null)
                    "📅 Mindesthaltbarkeitsdatum auswählen"
                else
                    "📅 ${formatDate(mindesthaltbarBis!!)}"
            )
        }
    }

    if (showDatePicker) {

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            colors = DatePickerDefaults.colors(

                containerColor = Color.White

            ),

            confirmButton = {

                TextButton(

                    onClick = {

                        mindesthaltbarBis =
                            datePickerState.selectedDateMillis

                        showDatePicker = false

                    }

                ) {

                    Text("OK")

                }

            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showDatePicker = false

                    }

                ) {

                    Text("Abbrechen")

                }

            }

        ) {

            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(

                    containerColor = MaterialTheme.colorScheme.surface

                )
            )

        }

    }
}

private fun formatDate(time: Long): String {

    val formatter = SimpleDateFormat(
        "dd.MM.yyyy",
        Locale.getDefault()
    )

    return formatter.format(Date(time))
}