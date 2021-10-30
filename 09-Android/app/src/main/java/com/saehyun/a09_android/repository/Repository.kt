package com.saehyun.a09_android.repository

import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.model.request.PostRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.AuthReissueResponse
import com.saehyun.a09_android.model.response.PostResponse
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

    suspend fun authReissue(refreshToken: String) : Response<AuthReissueResponse> {
        return ApiProvider.api.authReissue(refreshToken)
    }

    suspend fun post(page: Int, size: Int) : Response<PostResponse> {
        return ApiProvider.api.post(page, size)
    }

    suspend fun searchPost(keyword: String, page: Int, size: Int) : Response<PostResponse> {
        return ApiProvider.api.searchPost(keyword, page, size)
    }
}