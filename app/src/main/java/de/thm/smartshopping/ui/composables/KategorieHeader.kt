package de.thm.smartshopping.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.ui.util.getCategoryImage

@Composable
fun KategorieHeader(
	kategorieName: String,
	isExpanded: Boolean,
	artikelAnzahl: Int,
	onClick: () -> Unit,
) {

	val rotation = animateFloatAsState(
		targetValue = if (isExpanded) 180f else 0f,
		label = ""
	)

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 8.dp)
			.clickable { onClick() },

		shape = RoundedCornerShape(24.dp),

		elevation = CardDefaults.cardElevation(
			defaultElevation = 3.dp
		)
	) {

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(170.dp)
		) {

			Image(
				painter = painterResource(getCategoryImage(kategorieName)),
				contentDescription = kategorieName,
				modifier = Modifier.fillMaxSize(),
				contentScale = ContentScale.Crop
			)

			Surface(
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.fillMaxWidth(),

				color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),

				shape = RoundedCornerShape(
					topStart = 20.dp,
					topEnd = 20.dp
				)
			) {

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							horizontal = 18.dp,
							vertical = 14.dp
						),

					verticalAlignment = Alignment.CenterVertically
				) {

					Column(
						modifier = Modifier.weight(1f)
					) {

						Text(
							text = kategorieName,
							style = MaterialTheme.typography.titleLarge,
							fontWeight = FontWeight.Bold
						)

						Text(
							text = "$artikelAnzahl Artikel",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}

					Icon(
						imageVector = Icons.Default.KeyboardArrowDown,
						contentDescription = null,

						modifier = Modifier.rotate(rotation.value)
					)
				}
			}
		}
	}
}