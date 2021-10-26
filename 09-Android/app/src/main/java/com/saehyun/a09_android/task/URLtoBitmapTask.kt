package com.saehyun.a09_android.task

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.URL

class URLtoBitmapTask() : AsyncTask<Void, Void, Bitmap>() {

    lateinit var url: URL
    override fun doInBackground(vararg params: Void?): Bitmap {
        val bitmap = BitmapFactory.decodeStream(url.openStream())
        return bitmap
    }

    override fun onPreExecute() {
        super.onPreExecute()

    }
    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)
    }
}