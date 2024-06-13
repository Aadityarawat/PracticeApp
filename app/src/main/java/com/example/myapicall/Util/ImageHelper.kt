package com.example.myapicall.Util

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class ImageHelper {
    /** create image temporary file and return the file */
    fun createImageFile(context : Context) : File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }

    /** Convert the image URI to File and return that file */
    fun uriToFile(context : Context, uri : Uri) : File? {
        context.contentResolver.openInputStream(uri)?.let { inputStream ->
            val tempFile : File = createImageFile(context)
            val fileOutputStream = FileOutputStream(tempFile)

            inputStream.copyTo(fileOutputStream)
            inputStream.close()
            fileOutputStream.close()

            return tempFile
        }
        return null
    }
}