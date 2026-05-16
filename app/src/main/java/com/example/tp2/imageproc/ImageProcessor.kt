package com.example.tp2.imageproc

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory.decodeByteArray

interface ImageProcessor{
    val name: String
    fun apply(src: Bitmap): Bitmap
}

class ToJpgProcessor: ImageProcessor{
    override val name = "Convertir a JPG"

    override fun apply(src: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        return decodeByteArray(byteArray, 0, byteArray.size)
    }
}

class ToPngProcessor: ImageProcessor{
    override val name = "Convertir a PNG"

    override fun apply(src: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        return decodeByteArray(byteArray, 0, byteArray.size)
    }
}