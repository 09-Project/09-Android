package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class PostPostViewModel(private val repository: Repository) : ViewModel() {
    val authPostResponse : MutableLiveData<Response<Void>> = MutableLiveData()

    fun authPostSearch(title: String, content: String, price: Int, transactionRegion: String, openChatLink: String, image: String) {
        viewModelScope.launch {
            val response = repository.postPost(title, content, price, transactionRegion, openChatLink, image)
            authPostResponse.value = response
        }
    }
}