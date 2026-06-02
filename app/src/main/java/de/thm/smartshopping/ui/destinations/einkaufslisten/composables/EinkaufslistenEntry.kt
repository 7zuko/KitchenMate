package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EinkaufslisteEntry(
	einkaufsliste: Einkaufsliste,
	onClick: () -> Unit = {},
	onLongClick: () -> Unit = {}
) {

	val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
	val formatErstellDatum = formatter.format(einkaufsliste.bearbeitetAm)

	ElevatedCard(
		modifier = Modifier
			.padding(horizontal = 12.dp, vertical = 6.dp)
			.fillMaxWidth()
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick
			),

		shape = RoundedCornerShape(24.dp),

		colors = CardDefaults.elevatedCardColors(
			containerColor = MaterialTheme.colorScheme.surface
		),

		elevation = CardDefaults.elevatedCardElevation(
			defaultElevation = 3.dp
		)
	) {

		Column(
			modifier = Modifier.padding(18.dp)
		) {

			Text(
				text = einkaufsliste.name,
				style = MaterialTheme.typography.titleLarge,
				maxLines = 2
			)

			Spacer(modifier = Modifier.height(12.dp))

			Row(
				verticalAlignment = Alignment.CenterVertically
			) {

				Text(
					text = "🛒 ${einkaufsliste.artikel.size} Artikel",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)

				Spacer(modifier = Modifier.weight(1f))

				Surface(
					shape = RoundedCornerShape(12.dp),
					color = MaterialTheme.colorScheme.primaryContainer
				) {

					Row(
						modifier = Modifier.padding(
							horizontal = 10.dp,
							vertical = 6.dp
						),

						verticalAlignment = Alignment.CenterVertically
					) {

						Icon(
							modifier = Modifier.size(16.dp),
							imageVector = Icons.Default.EditCalendar,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.onPrimaryContainer
						)

						Spacer(modifier = Modifier.width(6.dp))

						Text(
							text = formatErstellDatum,
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onPrimaryContainer
						)
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun EinkaufslisteEntryPreview() {
	SmartShoppingTheme {
		EinkaufslisteEntry(einkaufsliste = Einkaufsliste("1", "Einkaufsliste 1"))
	}
}