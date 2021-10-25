package com.saehyun.a09_android.remote

import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
}