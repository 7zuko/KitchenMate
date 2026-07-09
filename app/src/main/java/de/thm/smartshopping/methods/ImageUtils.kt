package de.thm.smartshopping.methods

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageUtils {

    fun saveImageToInternalStorage(
        context: Context,
        uri: Uri
    ): String {

        val inputStream =
            context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Bild konnte nicht geöffnet werden")

        val fileName = "${UUID.randomUUID()}.jpg"

        val file = File(
            context.filesDir,
            fileName
        )

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        inputStream.close()

        return file.absolutePath
    }
}