package de.thm.smartshopping.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface

@Composable
fun OhneKategorieHeader(
	artikelAnzahl: Int
) {
	Surface(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 6.dp),

		shape = RoundedCornerShape(16.dp),

		color = MaterialTheme.colorScheme.surfaceVariant
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 18.dp, vertical = 14.dp),

			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {

			Text(
				text = "Ohne Kategorie",
				style = MaterialTheme.typography.titleLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)

			Text(
				text = artikelAnzahl.toString(),
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
fun OhneKategorieHeaderPreview() {
	SmartShoppingTheme {
		OhneKategorieHeader(artikelAnzahl = 10)
	}
}