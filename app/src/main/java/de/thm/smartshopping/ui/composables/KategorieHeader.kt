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
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun KategorieHeader(
	kategorieName: String,
	isExpanded: Boolean,
	artikelAnzahl: Int,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.background(MaterialTheme.colorScheme.primaryContainer)
			.padding(horizontal = 16.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(
			text = kategorieName,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		Row {
			Text(
				text = "($artikelAnzahl)",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Icon(
				imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
				contentDescription = if (isExpanded) "Einklappen" else "Ausklappen",
				tint = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
	}
	HorizontalDivider()
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