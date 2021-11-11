package com.saehyun.a09_android.model.data

data class MemberInfo(
        val member_id: Int,
        val member_name: String,
        val member_introduction: String,
        val member_profile: String,
        val posts_count: Int,
        val every_like_counts: Int
)
