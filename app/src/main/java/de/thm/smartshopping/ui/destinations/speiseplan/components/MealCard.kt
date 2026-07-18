package de.thm.smartshopping.ui.destinations.speiseplan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.destinations.speiseplan.models.MealType

@Composable
fun MealRow(
    meal: MealType,
    plannedRecipe: String?,
    duration: Int?,
    portions: Int?,
    onAddClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "${meal.emoji} ${meal.title}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(4.dp))

            if (plannedRecipe == null) {

                Text(
                    text = "Noch kein Rezept geplant",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Tippe auf + um ein Rezept auszuwählen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            } else {

                Text(
                    text = plannedRecipe,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "${duration ?: "-"} Min • ${portions ?: "-"} Portionen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            onClick = onAddClick
        ) {

            Icon(
                imageVector =
                    if (plannedRecipe == null)
                        Icons.Outlined.AddCircle
                    else
                        Icons.Outlined.Edit,

                contentDescription = null,

                modifier = Modifier.padding(10.dp)
            )

        }
    }
}