package com.papb.motorescue.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUtils {
    fun fileToBase64(file: File): String {
        // 1. Ubah File jadi Bitmap
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)

        // 2. Kecilkan ukuran (Resize)
        val resizedBitmap = getResizedBitmap(bitmap, 600)

        // 3. Kompres ke JPEG Kualitas 50%
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // 4. Ubah jadi String Base64
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}