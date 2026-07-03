package de.thm.smartshopping.ui.destinations.rezepte.composables // Or your preferred package

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.R
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import androidx.compose.material3.Surface
import de.thm.smartshopping.data.Rezept

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptCard(
	rezept: Rezept,
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {}
) {
	ElevatedCard(
		onClick = onClick,
		modifier = modifier
			.fillMaxWidth()
			.padding(2.dp), // Padding around each card
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.outlinedCardColors(
			containerColor = MaterialTheme.colorScheme.surface
		)
	) {
		Column(
			modifier = Modifier.fillMaxSize(), // Fill the card
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// Image Placeholder
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(16f / 9f) // Common aspect ratio for images
					.padding(8.dp) // Padding inside the card before the image
					.clip(RoundedCornerShape(18.dp)), // Rounded corners for the image box
				contentAlignment = Alignment.Center
			) {
				Image(
					painter = painterResource(
						id = R.drawable.ic_placeholder_recipe
					),
					contentDescription = rezept.name,
					contentScale = ContentScale.Crop,
					modifier = Modifier.fillMaxSize()
				)
			}

			Spacer(modifier = Modifier.height(8.dp))

			Surface(
				shape = RoundedCornerShape(12.dp),
				color = MaterialTheme.colorScheme.secondaryContainer
			) {
				Text(
					modifier = Modifier.padding(
						horizontal = 10.dp,
						vertical = 6.dp
					),

					text =
						if (rezept.zubereitungszeit > 0)
							"⏱ ${rezept.zubereitungszeit} Min"
						else
							"⏱ Unbekannt",
					style = MaterialTheme.typography.bodySmall
				)
			}

			Spacer(modifier = Modifier.height(8.dp))
			// Recipe Name
			Text(
				text = rezept.name,
				style = MaterialTheme.typography.titleLarge,
				textAlign = TextAlign.Center,

				modifier = Modifier
					.padding(horizontal = 12.dp)
					.padding(bottom = 16.dp),

				maxLines = 2,
				overflow = TextOverflow.Ellipsis
			)
		}
	}
}

@Preview(showBackground = true, widthDp = 200)
@Composable
fun RezeptCardPreview() {
	SmartShoppingTheme {
		RezeptCard(
			rezept = Rezept(id = "1", name = "Sehr Leckerer Kuchen mit vielen Zutaten")
		)
	}
}
