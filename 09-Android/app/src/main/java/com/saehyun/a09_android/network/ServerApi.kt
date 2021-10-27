package com.saehyun.a09_android.network

import com.saehyun.a09_android.model.data.RcProductRvData
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.AuthReissueResponse
import com.saehyun.a09_android.model.response.PostResponse
import retrofit2.Response
import retrofit2.http.*

interface ServerApi {

    // Auth

    // 회원가입
    @POST("auth/signup")
    suspend fun authSignUp(
        @Body authSignUpRequest: AuthSignUpRequest
    ) : Response<Void>

    // 로그인
    @POST("auth/login")
    suspend fun authLogin(
        @Body authLoginRequest: AuthLoginRequest
    ) : Response<AuthLoginResponse>

    // 토큰 재발급
    @PUT("auth/reissue")
    suspend fun authReissue(
        @Header("x-refresh-token") refreshToken: String
    ) : Response<AuthReissueResponse>

    // Post
    @GET("post")
    suspend fun post() : Response<List<PostResponse>>

}