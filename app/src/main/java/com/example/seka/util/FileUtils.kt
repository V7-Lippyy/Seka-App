package com.example.seka.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    suspend fun copyUriToFile(context: Context, uri: Uri, destinationFile: File) = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destinationFile).use { output ->
                input.copyTo(output)
            }
        }
    }

    fun loadBitmapFromFile(filePath: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(filePath)
        } catch (e: Exception) {
            null
        }
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: Exception) {
            null
        }
    }
}