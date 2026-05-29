package de.thm.smartshopping.ui.destinations.rezepte.composables // Or your preferred package

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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

// Mock data class for preview and structure
data class RezeptMock(
	val id: String,
	val name: String,
	val imageUrl: Int? = null // Using Int for drawable resource ID
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptCard(
	rezept: RezeptMock,
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {}
) {
	OutlinedCard(
		onClick = onClick,
		modifier = modifier
			.fillMaxWidth()
			.padding(8.dp), // Padding around each card
		shape = RoundedCornerShape(12.dp),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
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
					.clip(RoundedCornerShape(8.dp)), // Rounded corners for the image box
				contentAlignment = Alignment.Center
			) {
				Image(
					painter = painterResource(id = rezept.imageUrl ?: R.drawable.ic_placeholder_recipe), // Replace with your placeholder
					contentDescription = rezept.name,
					contentScale = ContentScale.Crop,
					modifier = Modifier.fillMaxSize()
				)
			}

			Spacer(modifier = Modifier.height(8.dp))

			// Recipe Name
			Text(
				text = rezept.name,
				style = MaterialTheme.typography.titleMedium,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.padding(horizontal = 8.dp)
					.padding(bottom = 12.dp), // Padding below text
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
			rezept = RezeptMock(id = "1", name = "Sehr Leckerer Kuchen mit vielen Zutaten")
		)
	}
}
