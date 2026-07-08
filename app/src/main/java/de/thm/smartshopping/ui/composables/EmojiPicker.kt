package de.thm.smartshopping.ui.composables

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val shoppingEmojis = listOf(
    "🛒","🍎","🍏","🍐","🍊","🍋","🍌","🍉","🍇","🍓",
    "🥕","🥔","🧅","🧄","🥦","🥬","🌽","🍅","🥒","🌶️",
    "🥛","🧀","🧈","🥚",
    "🍞","🥐","🥖","🥨",
    "🥩","🍗","🥓","🌭",
    "🥤","🧃","☕","🫖",
    "🍫","🍪","🍰","🍬",
    "🧻","🧼","🧽","🧴",
    "🐶","🐱","🐟"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPicker(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {

        Text(
            text = "Emoji auswählen",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.padding(12.dp)
        ) {

            items(shoppingEmojis) { emoji ->

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {

                            onEmojiSelected(emoji)

                        },

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = emoji,
                        fontSize = 32.sp
                    )
                }
            }
        }
    }
}