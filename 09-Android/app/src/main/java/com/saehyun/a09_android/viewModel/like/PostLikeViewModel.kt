package com.saehyun.a09_android.viewModel.like

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class PostLikeViewModel(private val repository: Repository) : ViewModel() {
    val authPostLikeResponse : MutableLiveData<Response<Void>> = MutableLiveData()

    fun authPostLikeSearch(postId: Int) {
        viewModelScope.launch {
            val response = repository.postLike(postId)
            authPostLikeResponse.value = response
        }
    }
}