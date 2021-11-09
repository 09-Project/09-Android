package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.PostOtherResponse
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.REFRESH_TOKEN
import kotlinx.coroutines.launch
import retrofit2.Response

class PostOtherViewModel(private val repository: Repository) : ViewModel() {
    val authPostOtherResponse : MutableLiveData<Response<List<PostOtherResponse>>> = MutableLiveData()

    fun authPostOther() {
        viewModelScope.launch {
            val response = repository.postOther()
            authPostOtherResponse.value = response
        }
    }
}