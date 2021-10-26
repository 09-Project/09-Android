package com.saehyun.a09_android.repository

import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.network.ApiProvider
import retrofit2.Response

class Repository {
    suspend fun authSignUP(name: String, username: String, password: String) : Response<Void> {
        val authSignUpRequest = AuthSignUpRequest(name, username, password)
        return ApiProvider.api.authSignUp(authSignUpRequest)
    }

    suspend fun authLogin(username : String, password : String) : Response<AuthLoginResponse> {
        val authLoginRequest = AuthLoginRequest(username, password)
        return ApiProvider.api.authLogin(authLoginRequest)
    }
}