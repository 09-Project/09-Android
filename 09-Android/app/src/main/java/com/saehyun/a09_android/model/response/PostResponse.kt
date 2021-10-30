package com.saehyun.a09_android.model.response

data class PostResponse(
    val count: Int,
    val posts: List<PostValue>
)