package com.saehyun.a09_android.model.response

import com.google.gson.annotations.SerializedName

data class AuthReissueResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)
