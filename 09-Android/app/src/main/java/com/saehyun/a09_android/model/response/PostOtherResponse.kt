package com.saehyun.a09_android.model.response

data class PostOtherResponse(
        val id: Int,
        val title: String,
        val image: String,
        val completed: String,
        val liked: Boolean
)
