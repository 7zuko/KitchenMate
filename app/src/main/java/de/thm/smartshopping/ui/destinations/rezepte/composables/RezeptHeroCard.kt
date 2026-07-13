package de.thm.smartshopping.ui.destinations.rezepte.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import de.thm.smartshopping.R
import de.thm.smartshopping.data.Rezept
import androidx.compose.ui.res.painterResource
import java.io.File
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun RezeptHeroCard(
    rezept: Rezept,
    onEditName: () -> Unit,
    onEditImage: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(24.dp)
    ) {

        Box {

            if (rezept.bildPfad != null) {

                AsyncImage(
                    model = File(rezept.bildPfad),
                    contentDescription = rezept.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable{
                            onEditImage()
                        },
                    contentScale = ContentScale.Crop
                )

            } else {

                Image(
                    painter = painterResource(R.drawable.ic_placeholder_recipe),
                    contentDescription = rezept.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable {
                            onEditImage()
                        },
                    contentScale = ContentScale.Crop
                )

            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.65f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = rezept.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )

                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        IconButton(
                            onClick = onEditName
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.18f)
                ) {

                    Text(
                        text = "⏱ ${rezept.zubereitungszeit} Minuten",
                        color = Color.White,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }
        }
    }
}