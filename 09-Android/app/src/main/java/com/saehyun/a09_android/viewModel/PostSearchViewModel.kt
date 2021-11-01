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

class PostSearchViewModel(private val repository: Repository) : ViewModel() {
    val authPostSearchResponse : MutableLiveData<Response<PostResponse>> = MutableLiveData()

    fun authPostSearch(keyword: String, page: Int, size: Int) {
        viewModelScope.launch {
            val response = repository.searchPost(keyword, page, size)
            authPostSearchResponse.value = response
        }
    }
}