package com.saehyun.a09_android.model.response

data class PostResponse(
    val id: String,
    val title: String,
    val price: String,
    val transaction_region: String,
    val purpose: String,
    val completed: String,
    val created_date: String,
    val updated_date: String,
    val image: String
)
