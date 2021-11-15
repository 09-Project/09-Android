package com.saehyun.a09_android.model.response

import com.saehyun.a09_android.model.data.MemberInfo

data class PostGetResponse(
    val title: String,
    val content: String,
    val price: Int,
    val transaction_region: String,
    val open_chat_link: String,
    val purpose: String,
    val completed: String,
    val created_date: String,
    val updated_date: String,
    val image: String,

    val member_info: MemberInfo,

    val get_likes: Int,
    val liked: Boolean,
    val mine: Boolean
)