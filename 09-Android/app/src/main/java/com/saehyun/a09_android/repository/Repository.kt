package com.saehyun.a09_android.repository

import android.util.Log
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.model.request.PostPostRequest
import com.saehyun.a09_android.model.request.PostRequest
import com.saehyun.a09_android.model.response.*
import com.saehyun.a09_android.network.ApiProvider
import com.saehyun.a09_android.util.ACCESS_TOKEN
import com.saehyun.a09_android.util.REFRESH_TOKEN
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class Repository {
    private val TAG = "Repository"

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

    suspend fun postOther(refreshToken: String) : Response<List<PostOtherResponse>> {
        return ApiProvider.api.otherPost(refreshToken)
    }

    suspend fun postGet(pageId: Int) : Response<PostGetResponse> {
        return ApiProvider.api.getPost(pageId)
    }

    suspend fun postGroupBuy(title: RequestBody, content: RequestBody, price: RequestBody, transactionRegion: RequestBody,
                         openChatLink: RequestBody, image: MultipartBody.Part) : Response<Void> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.postGroupBuy(tempToken, title, content, price, transactionRegion, openChatLink, image)
    }
}