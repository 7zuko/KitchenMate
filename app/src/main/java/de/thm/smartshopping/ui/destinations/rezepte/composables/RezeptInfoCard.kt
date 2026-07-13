package de.thm.smartshopping.ui.destinations.rezepte.composables

import android.app.appsearch.observer.SchemaChangeInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RezeptInfoCard(
    zubereitungszeit: Int,
    anzahlZutaten: Int,
    vorhandeneZutaten: Int,
    teilweiseVorhandeneZutaten: Int,
    fehlendeZutaten: Int,
    schwierigkeit: String,
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (schwierigkeit) {
                        "Einfach" -> Color(0xFFE8F5E9)
                        "Mittel" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFFFEBEE)
                    }
                ) {
                    Text(
                        text = schwierigkeit,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                    )
                }

                Text("🥕 $anzahlZutaten Zutaten")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),

                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("🟢 $vorhandeneZutaten")

                Text("🟡 $teilweiseVorhandeneZutaten")

                Text("🔴 $fehlendeZutaten")
            }
        }
    }
}