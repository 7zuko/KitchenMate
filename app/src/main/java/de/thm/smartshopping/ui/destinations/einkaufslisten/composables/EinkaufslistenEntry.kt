package de.thm.smartshopping.ui.destinations.einkaufslisten.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.thm.smartshopping.data.Einkaufsliste
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EinkaufslisteEntry(
	einkaufsliste: Einkaufsliste,
	onClick: () -> Unit = {},
	onLongClick: () -> Unit = {}
) {
	val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
	val formatErstellDatum = formatter.format(einkaufsliste.bearbeitetAm)

	Box(
		modifier = Modifier
			.height(60.dp)
			.fillMaxWidth()
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick
			),
		//.background(MaterialTheme.colorScheme.background),
		contentAlignment = Alignment.CenterStart,
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Start
		) {
			Text(
				modifier = Modifier
					.padding(start = 24.dp)
					.weight(3f),
				text = einkaufsliste.name,
				maxLines = 2
			)
			Text(
				modifier = Modifier
					.padding(start = 12.dp)
					.weight(2f),
				text = "${einkaufsliste.artikel.size} Artikel"
			)
			Row(
				modifier = Modifier
					.padding(end = 12.dp)
					.weight(2f)
					.background(
						color = MaterialTheme.colorScheme.primaryContainer,
						shape = ShapeDefaults.Medium
					),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Center
			) {
				Icon(
					modifier = Modifier.size(16.dp),
					imageVector = Icons.Default.EditCalendar,
					contentDescription = "Erstelldatum",
					tint = MaterialTheme.colorScheme.onPrimaryContainer
				)
				Text(
					modifier = Modifier.padding(start = 3.dp),
					color = MaterialTheme.colorScheme.onPrimaryContainer,
					fontSize = 12.sp,
					text = formatErstellDatum
				)
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