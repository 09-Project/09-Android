package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val repository: Repository) : ViewModel() {
    val authLoginResponse : MutableLiveData<Response<AuthLoginResponse>> = MutableLiveData()

    fun authLogin(username : String, password : String) {
        viewModelScope.launch {
            val response = repository.authLogin(username, password)
            authLoginResponse.value = response
        }
    }
}