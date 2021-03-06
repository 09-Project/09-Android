package com.saehyun.a09_android.network

import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ServerApi {

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
        @Header("Authorization") accessToken: String,
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
            @Header("Authorization") accessToken: String
    ) : Response<List<PostOtherResponse>>

    // 상품 보기
    @GET("post/{post-id}")
    suspend fun getPost(
            @Header("Authorization") accessToken: String,
            @Path("post-id") postId :Int
    ) : Response<PostGetResponse>

    // 상품 올리기 (공동구매)
    @Multipart
    @POST("post")
    suspend fun postGroupBuy(
            @Header("Authorization") accessToken: String,
            @Part("title") title: RequestBody?,
            @Part("content") content: RequestBody?,
            @Part("price") price: RequestBody?,
            @Part("transactionRegion") transactionRegion: RequestBody?,
            @Part("openChatLink") openChatLink: RequestBody?,
            @Part image: MultipartBody.Part?
    ) : Response<Void>

    // 상품 올리기 (무료나눔)
    @Multipart
    @POST("post")
    suspend fun postSharing(
            @Header("Authorization") accessToken: String,
            @Part("title") title: RequestBody?,
            @Part("content") content: RequestBody?,
            @Part("transactionRegion") transactionRegion: RequestBody?,
            @Part("openChatLink") openChatLink: RequestBody?,
            @Part image: MultipartBody.Part?
    ) : Response<Void>

    // 마이페이지 보기
    @GET("member/my-page")
    suspend fun myPage(
        @Header("Authorization") accessToken: String,
    ) : Response<MemberShowResponse>

    // 찜하기
    @POST("like/{post-id}")
    suspend fun likePost(
        @Header("Authorization") accessToken: String,
        @Path("post-id") postId: Int
    ) : Response<Void>

    // 찜하기 취소
    @DELETE("like/{post-id}")
    suspend fun deleteLikePost(
        @Header("Authorization") accessToken: String,
        @Path("post-id") postId: Int
    ) : Response<Void>

    // 찜한 게시글 보기
    @GET("member/like")
    suspend fun membertLike(
        @Header("Authorization") accessToken: String,
    ) : Response<List<PostValue>>

    // 진행중인 게시글
    @GET("/member/in-progress/{member-id}")
    suspend fun memberInProgress(
        @Header("Authorization") accessToken: String,
        @Path("member-id") memberId: String
    ) : Response<List<PostValue>>

    // 진행중인 게시글
    @GET("/member/completed/{member-id}")
    suspend fun memberCompleted(
        @Header("Authorization") accessToken: String,
        @Path("member-id") memberId: String
    ) : Response<List<PostValue>>

    // 상품 삭제하기
    @DELETE("post/{post-id}")
    suspend fun postDelete(
        @Header("Authorization") accessToken: String,
        @Path("post-id") postId: String
    ) : Response<Void>

    // 프로필 보기
    @GET("member/{member-id}")
    suspend fun memberShow(
        @Header("Authorization") accessToken: String,
        @Path("member-id") memberId: String
    ) : Response<MemberShowResponse>

    // 종료 여부 변경하기
    @PUT("post/{post-id}")
    suspend fun postComplete(
        @Header("Authorization") accessToken: String,
        @Path("post-id") postId: String
    ) : Response<Void>

    // 회원 정보 변경
    @Multipart
    @PATCH("member/information")
    suspend fun memberInformation(
        @Header("Authorization") accessToken: String,
        @Part("name") name: RequestBody?,
        @Part("introduction") introduction: RequestBody?,
        @Part profileUrl: MultipartBody.Part?
    ) : Response<Void>
}