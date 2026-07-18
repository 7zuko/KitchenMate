package de.thm.smartshopping.ui.destinations.speiseplan.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.Rezept

@Composable
fun RecipeCard(
    rezept: Rezept,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Icon(
                imageVector = Icons.Outlined.RestaurantMenu,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = rezept.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${rezept.zubereitungszeit} Min • ${rezept.portionen} Portionen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

        }

        HorizontalDivider()

    }

}