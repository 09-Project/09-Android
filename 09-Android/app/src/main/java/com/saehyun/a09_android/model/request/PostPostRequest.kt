package com.saehyun.a09_android.model.request

data class PostPostRequest(
    val title: String,
    val content: String,
    val price: Int,
    val transactionRegion: String,
    val openChatLink: String,
    val image: String
)
