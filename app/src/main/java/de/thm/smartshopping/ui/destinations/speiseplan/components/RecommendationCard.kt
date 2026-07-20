package de.thm.smartshopping.ui.destinations.speiseplan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.RecipeRecommendation

@Composable
fun RecommendationCard(
    recommendations: List<RecipeRecommendation>
) {

    if (recommendations.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F5F0)
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {

                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(10.dp)
                    )

                }

                Spacer(Modifier.width(12.dp))

                Text(
                    "KitchenMate empfiehlt",
                    style = MaterialTheme.typography.titleLarge
                )

            }

            recommendations.forEach { recommendation ->

                Column {

                    Text(
                        text = recommendation.rezept.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = recommendation.reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Score: ${recommendation.score}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                }

            }

        }

    }

}