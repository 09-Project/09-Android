package com.saehyun.a09_android.util

import android.content.Context
import android.widget.Toast


object ToastUtil {
    fun print(context: Context?, str: String?) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }
}