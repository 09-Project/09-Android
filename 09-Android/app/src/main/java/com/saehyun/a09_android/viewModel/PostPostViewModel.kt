package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class PostPostViewModel(private val repository: Repository) : ViewModel() {
    val authPostResponse : MutableLiveData<Response<Void>> = MutableLiveData()

    fun authPost(postImg: MultipartBody.Part, data: HashMap<String, RequestBody>) {
        viewModelScope.launch {
            val response = repository.postPost(postImg, data)
            authPostResponse.value = response
        }
    }
}