package com.gundogar.lineupapp.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.gundogar.lineupapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ShareUtil {

    fun shareLineupImage(context: Context, bitmap: Bitmap, teamName: String) {
        try {
            // Save to cache directory for sharing
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()

            val fileName = "${sanitizeFileName(teamName)}_lineup_${System.currentTimeMillis()}.png"
            val file = File(cachePath, fileName)

            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }

            // Get content URI via FileProvider
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            // Create share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_lineup_subject, teamName))
                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_lineup_text, teamName))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_lineup)))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.share_failed), Toast.LENGTH_SHORT).show()
        }
    }

    fun saveLineupImage(context: Context, bitmap: Bitmap, teamName: String) {
        try {
            val fileName = "${sanitizeFileName(teamName)}_lineup_${System.currentTimeMillis()}.png"

            val outputStream: OutputStream?
            val imageUri: Uri?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ uses MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LineUp")
                }

                val resolver = context.contentResolver
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                outputStream = imageUri?.let { resolver.openOutputStream(it) }

            } else {
                // Legacy storage for older Android versions
                @Suppress("DEPRECATION")
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val lineupDir = File(imagesDir, "LineUp")
                lineupDir.mkdirs()

                val file = File(lineupDir, fileName)
                outputStream = FileOutputStream(file)
                imageUri = Uri.fromFile(file)
            }

            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }

            Toast.makeText(context, context.getString(R.string.save_success), Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.save_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9]"), "_")
            .take(30)
            .lowercase()
    }
}
