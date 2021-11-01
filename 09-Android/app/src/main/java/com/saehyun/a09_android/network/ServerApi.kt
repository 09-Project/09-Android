package com.saehyun.a09_android.network

import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.model.request.PostRequest
import com.saehyun.a09_android.model.response.*
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

    // 전체 상품 보기
    @GET("post")
    suspend fun post(
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : Response<PostResponse>

    // 상품 검색하기
    @GET("post/search")
    suspend fun searchPost(
            @Query("keyword") keyword: String,
            @Query("page") page: Int,
            @Query("size") size: Int
    ) : Response<PostResponse>

    // 다른 추천 상품 보기
    @GET("post/other")
    suspend fun otherPost(
            @Header("x-refresh-token") refreshToken: String
    ) : Response<List<PostOtherResponse>>

    // 상품 보기
    @GET("post/{post-id}")
    suspend fun getPost(
            @Path("post-id") postId :Int
    ) : Response<PostGetResponse>
}