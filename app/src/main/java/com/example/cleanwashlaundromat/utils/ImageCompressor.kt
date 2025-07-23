package com.example.cleanwashlaundromat.utils

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {
    fun compress(application: Application, uri: Uri): File {
        val inputStream = application.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Tentukan lokasi file hasil kompresi di cache
        val outputDir = application.cacheDir
        val outputFile = File(outputDir, "compressed_image.jpg")

        // Lakukan kompresi
        FileOutputStream(outputFile).use { out ->
            // Kompres ke format JPEG dengan kualitas 80%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        return outputFile
    }
}