package com.saehyun.a09_android.model.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostValue(
    val id: String,
    val title: String,
    val price: String,
    val transaction_region: String,
    val purpose: String,
    val completed: String,
    val created_date: String,
    val updated_date: String,
    val image: String,
    var liked: Boolean
)
