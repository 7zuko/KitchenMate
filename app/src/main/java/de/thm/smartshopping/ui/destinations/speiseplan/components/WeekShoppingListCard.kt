package de.thm.smartshopping.ui.destinations.speiseplan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeekShoppingListCard(
    onCreateShoppingList: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null
            )

            Text(
                text = "Einkaufsliste für die Woche",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Alle geplanten Rezepte dieser Woche werden zusammengeführt und mit deinem Vorrat abgeglichen.",
                style = MaterialTheme.typography.bodyMedium
            )

            FilledTonalButton(
                onClick = onCreateShoppingList,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Jetzt erstellen")
            }

        }

    }

}