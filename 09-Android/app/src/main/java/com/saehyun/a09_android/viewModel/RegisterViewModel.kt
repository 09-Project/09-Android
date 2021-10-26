package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthSignUpRequest
import com.saehyun.a09_android.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    val authSignUpResponse : MutableLiveData<Response<Void>> = MutableLiveData()

    fun authSignUp(name : String, username : String, password : String) {
        viewModelScope.launch {
            val response = repository.authSignUP(name, username, password)
            authSignUpResponse.value = response
        }
    }
}