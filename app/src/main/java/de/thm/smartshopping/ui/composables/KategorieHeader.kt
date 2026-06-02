package de.thm.smartshopping.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface

@Composable
fun KategorieHeader(
	kategorieName: String,
	isExpanded: Boolean,
	artikelAnzahl: Int,
	onClick: () -> Unit,
) {
	Surface(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 6.dp)
			.clickable(onClick = onClick),

		shape = RoundedCornerShape(16.dp),

		color = MaterialTheme.colorScheme.primaryContainer
	) {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 18.dp, vertical = 14.dp),

			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {

			Text(
				text = kategorieName,
				style = MaterialTheme.typography.titleLarge,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)

			Row(
				verticalAlignment = Alignment.CenterVertically
			) {

				Text(
					text = artikelAnzahl.toString(),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)

				Icon(
					imageVector =
						if (isExpanded)
							Icons.Filled.KeyboardArrowUp
						else
							Icons.Filled.KeyboardArrowDown,

					contentDescription = null,

					tint = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun KategorieHeaderPreview() {
	SmartShoppingTheme {
		KategorieHeader(
			kategorieName = "Milchprodukte",
			isExpanded = true,
			artikelAnzahl = 10,
			onClick = {}
		)
	}
}