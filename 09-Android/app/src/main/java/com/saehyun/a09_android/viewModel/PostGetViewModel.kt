package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.PostGetResponse
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class PostGetViewModel(private val repository: Repository) : ViewModel() {
    val authPostGetResponse : MutableLiveData<Response<PostGetResponse>> = MutableLiveData()

    fun authGetPost(pageId: Int) {
        viewModelScope.launch {
            val response = repository.postGet(pageId)
            authPostGetResponse.value = response
        }
    }
}