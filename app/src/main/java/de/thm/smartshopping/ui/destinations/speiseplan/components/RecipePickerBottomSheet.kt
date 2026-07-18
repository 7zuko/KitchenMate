package de.thm.smartshopping.ui.destinations.speiseplan.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.data.Rezept

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePickerBottomSheet(
    recipes: List<Rezept>,
    onRecipeSelected: (Rezept) -> Unit,
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,

        containerColor = Color(0xFFF8F5F0),

        tonalElevation = 0.dp,

        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFFC8BFB3)
            )
        }
    ) {

        Text(
            text = "Rezept auswählen",
            modifier = Modifier.padding(20.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                bottom = 24.dp
            )
        ) {

            items(recipes) { recipe ->

                RecipeCard(
                    rezept = recipe,
                    onClick = {
                        onRecipeSelected(recipe)
                    }
                )

            }

        }

    }

}