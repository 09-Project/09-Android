package com.saehyun.a09_android.model.response

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
    val get_likes: Int,
    val member_id: Int,
    val member_name: String,
    val member_introduction: String,
    val member_profile: String,
    val posts_count: Int,
    val every_like_counts: Int,
    val liked: Boolean
)
