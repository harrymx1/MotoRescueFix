package com.papb.motorescue.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUtils {

    fun fileToBase64(file: File): String {
        val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)

        val rotatedBitmap = rotateImageIfRequired(originalBitmap, file.absolutePath)

        val resizedBitmap = getResizedBitmap(rotatedBitmap, 600)

        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): ImageBitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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

    private fun rotateImageIfRequired(img: Bitmap, path: String): Bitmap {
        val ei = ExifInterface(path)
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(img, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(img, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(img, 270f)
            else -> img
        }
    }

    private fun rotate(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }
}