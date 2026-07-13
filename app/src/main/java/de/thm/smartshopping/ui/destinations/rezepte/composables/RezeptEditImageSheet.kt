package de.thm.smartshopping.ui.destinations.rezepte.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import de.thm.smartshopping.methods.ImageUtils
import de.thm.smartshopping.ui.composables.CustomModalSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RezeptEditImageSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onImageSelected: (String?) -> Unit
) {

    val context = LocalContext.current

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            imageUri = uri

        }

    CustomModalSheet(
        title = "Rezeptbild ändern",
        confirmButtonName = "Speichern",
        sheetState = sheetState,

        conditionConfirmEnabled = imageUri != null,

        onConfirmAfterClose = {

            val bildPfad =
                imageUri?.let {

                    ImageUtils.saveImageToInternalStorage(
                        context,
                        it
                    )

                }

            onImageSelected(
                bildPfad
            )

        },

        onDismissAfterClose = onDismiss
    ) {

        OutlinedButton(

            modifier = Modifier.fillMaxWidth(),

            onClick = {

                imagePicker.launch("image/*")

            }

        ) {

            Text("📷 Neues Bild auswählen")

        }

        imageUri?.let {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            AsyncImage(

                model = it,

                contentDescription = null,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),

                contentScale = ContentScale.Crop

            )

        }
    }
}