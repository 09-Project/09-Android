package com.saehyun.a09_android.model.response

import com.saehyun.a09_android.model.data.PostValue

data class PostResponse(
    val count: Int,
    val posts: List<PostValue>
)