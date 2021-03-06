package com.saehyun.a09_android.viewModel.post

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

class PostCompleteViewModel(private val repository: Repository) : ViewModel() {
    val postCompleteResponse : MutableLiveData<Response<Void>> = MutableLiveData()

    fun postComplete(postId: String) {
        viewModelScope.launch {
            val response = repository.postComplete(postId)
            postCompleteResponse.value = response
        }
    }
}