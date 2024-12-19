package com.grifffith.mindfuljournal.utils
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val file = File(context.filesDir, "journal_images") // Folder to store images
    if (!file.exists()) {
        file.mkdir() // Create the folder if it doesn't exist
    }
    val imageFile = File(file, "${System.currentTimeMillis()}.jpg") // Unique filename
    val outputStream = FileOutputStream(imageFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
        outputStream.write(buffer, 0, length)
    }
    outputStream.flush()
    outputStream.close()
    inputStream?.close()
    return imageFile.absolutePath // Return the saved file path
}
