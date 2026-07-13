package de.thm.smartshopping.ui.destinations.rezepte.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import de.thm.smartshopping.data.Einkaufsliste
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.composables.CustomModalSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptAddToShoppingListSheet(
    sheetState: SheetState,
    einkaufslisten: List<Einkaufsliste>,
    onNeueListe: () -> Unit,
    onListSelected: (Einkaufsliste) -> Unit,
    onDismiss: () -> Unit
) {
    CustomModalSheet(
        sheetState = sheetState,
        title = "Einkaufsliste auswählen",
        enableConfirmCancelButtons = false,
        onDismissAfterClose = onDismiss
    ) {
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 350.dp)
        ) {
            items(einkaufslisten) { liste ->

                ListItem(

                    leadingContent = {
                        Text("\uD83D\uDED2")
                    },

                    headlineContent = {
                        Text(liste.name)
                    },

                    supportingContent = {
                        Text("${liste.artikel.size} Artikel")
                    },

                    modifier = Modifier.clickable {
                        onListSelected(liste)
                    }
                )

                HorizontalDivider()
            }
        }
    }
}