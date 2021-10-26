package com.saehyun.a09_android.network

import com.saehyun.a09_android.util.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api : ServerApi by lazy {
        retrofit.create(ServerApi::class.java)
    }
}