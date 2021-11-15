package com.saehyun.a09_android.viewModel.post

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class PostViewModel(private val repository: Repository) : ViewModel() {
    val authPostResponse : MutableLiveData<Response<PostResponse>> = MutableLiveData()

    fun authPost(page: Int, size: Int) {
        viewModelScope.launch {
            val response = repository.post(page, size)
            authPostResponse.value = response
        }
    }
}