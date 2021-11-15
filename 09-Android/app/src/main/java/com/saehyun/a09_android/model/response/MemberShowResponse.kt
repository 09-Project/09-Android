package com.saehyun.a09_android.model.response

data class MemberShowResponse(
    val member_id: String,
    val name: String,
    val profile_url: String,
    val introduction: String,
    val all_posts_count: String,
    val get_likes_count: String,
    val in_progress_posts_count: String,
    val completed_posts_count: String,
    val like_posts_count: String
)
