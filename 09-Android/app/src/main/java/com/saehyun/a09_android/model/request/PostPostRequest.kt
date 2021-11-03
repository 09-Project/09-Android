package com.saehyun.a09_android.model.request

import okhttp3.MultipartBody
import java.io.File

data class PostPostRequest(
    val title: String,
    val content: String,
    val price: Int,
    val transactionRegion: String,
    val openChatLink: String,
    val image: MultipartBody.Part
)
