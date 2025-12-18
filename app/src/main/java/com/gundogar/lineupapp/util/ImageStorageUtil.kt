package com.gundogar.lineupapp.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object ImageStorageUtil {
    private const val PLAYER_IMAGES_DIR = "player_images"

    /**
     * Copies image from content URI to app-private storage.
     * Returns the file path as a string for persistence.
     */
    suspend fun copyImageToAppStorage(context: Context, sourceUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val imagesDir = File(context.filesDir, PLAYER_IMAGES_DIR).apply {
                    if (!exists()) mkdirs()
                }

                val fileName = "player_${UUID.randomUUID()}.jpg"
                val destFile = File(imagesDir, fileName)

                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                destFile.absolutePath
            } catch (e: Exception) {
                Toast.makeText(context,"Resim kaydedilemedi ${e}",Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    /**
     * Deletes player image from app storage.
     */
    fun deleteImage(imagePath: String?) {
        imagePath?.let {
            try {
                File(it).delete()
            } catch (e: Exception) {
                // Silently ignore deletion errors
            }
        }
    }
}
