package com.saehyun.a09_android.task

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

object ImageLoader {

    suspend fun loadImage(imageUrl: String): Bitmap? {
        val bmp: Bitmap? = null

        try {
            val url = URL(imageUrl)
            val stream = url.openStream()

            return BitmapFactory.decodeStream(stream)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bmp;
    }
}