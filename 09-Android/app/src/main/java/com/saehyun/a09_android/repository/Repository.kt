package com.saehyun.a09_android.repository

import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.model.response.*
import com.saehyun.a09_android.network.ApiProvider
import com.saehyun.a09_android.util.ACCESS_TOKEN
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

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
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.post(tempToken, page, size)
    }

    suspend fun searchPost(keyword: String, page: Int, size: Int) : Response<PostResponse> {
        return ApiProvider.api.searchPost(keyword, page, size)
    }

    suspend fun postOther() : Response<List<PostOtherResponse>> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.otherPost(tempToken)
    }

    suspend fun postGet(pageId: Int) : Response<PostGetResponse> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.getPost(tempToken, pageId)
    }

    suspend fun postGroupBuy(title: RequestBody, content: RequestBody, price: RequestBody, transactionRegion: RequestBody,
                              openChatLink: RequestBody, image: MultipartBody.Part) : Response<Void> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.postGroupBuy(tempToken, title, content, price, transactionRegion, openChatLink, image)
    }

    suspend fun postSharing(title: RequestBody, content: RequestBody, transactionRegion: RequestBody,
                             openChatLink: RequestBody, image: MultipartBody.Part) : Response<Void> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.postSharing(tempToken, title, content, transactionRegion, openChatLink, image)
    }

    suspend fun postLike(postId: Int) : Response<Void> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.likePost(tempToken, postId)
    }

    suspend fun deleteLikePost(postId: Int) : Response<Void> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.deleteLikePost(tempToken, postId)
    }

    suspend fun membertLike() : Response<List<PostValue>> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.membertLike(tempToken)
    }

    suspend fun myPage() : Response<MemberShowResponse> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.myPage(tempToken)
    }

    suspend fun memberInProgress(memberId: String) : Response<List<PostValue>> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.memberInProgress(tempToken, memberId)
    }

    suspend fun memberCompleted(memberId: String) : Response<List<PostValue>> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.memberCompleted(tempToken, memberId)
    }

    suspend fun postDelete(postId: String) : Response<Void> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.postDelete(tempToken, postId)
    }

    suspend fun memberShow(memberId: String) : Response<MemberShowResponse> {
        val tempToken: String = "Bearer " + ACCESS_TOKEN
        return ApiProvider.api.memberShow(tempToken, memberId)
    }
}