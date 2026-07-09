package de.thm.smartshopping.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn

@Composable
fun SearchTopBar(
    searchText: String,
    placeholder: String,
    onSearchTextChange: (String) -> Unit,
    onClose: () -> Unit,
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,

        placeholder = {
            Text(placeholder)
        },

        singleLine = true,

        shape = RoundedCornerShape(18.dp),

        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 12.dp, start = 12.dp)
            .widthIn(max = 340.dp),

        trailingIcon = {
            IconButton(
                onClick = onClose
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Suche schließen"
                )
            }
        }
    )
}