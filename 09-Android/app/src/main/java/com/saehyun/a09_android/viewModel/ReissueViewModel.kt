package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.AuthReissueResponse
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.REFRESH_TOKEN
import kotlinx.coroutines.launch
import retrofit2.Response

class ReissueViewModel(private val repository: Repository) : ViewModel() {
    val authReissueResponse : MutableLiveData<Response<AuthReissueResponse>> = MutableLiveData()

    fun authReissue(refreshToken : String) {
        viewModelScope.launch {
            val response = repository.authReissue(refreshToken)
            authReissueResponse.value = response
        }
    }
}