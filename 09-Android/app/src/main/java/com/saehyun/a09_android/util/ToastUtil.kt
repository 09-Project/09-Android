package com.saehyun.a09_android.util

import android.content.Context
import android.widget.Toast


object ToastUtil {
    fun print(context: Context?, str: String?) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

    fun errorPrint(context: Context?) {
        Toast.makeText(context, "예기지 못한 오류가 발생했습니다.\n지속될 시 고객센터에 문의해주세요.", Toast.LENGTH_SHORT).show()
    }
}