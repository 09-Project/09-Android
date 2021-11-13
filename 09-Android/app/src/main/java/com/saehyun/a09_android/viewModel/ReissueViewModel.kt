package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.request.AuthLoginRequest
import com.saehyun.a09_android.model.response.AuthLoginResponse
import com.saehyun.a09_android.model.response.AuthReissueResponse
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import kotlinx.coroutines.launch
import retrofit2.Response

class ReissueViewModel(private val repository: Repository) : ViewModel() {
    val authReissueResponse : MutableLiveData<Response<AuthReissueResponse>> = MutableLiveData()
    val toastMessage = MutableLiveData<String>()
    val refreshTokenExpiration = MutableLiveData<Boolean>()

    fun authReissue(refreshToken : String) {
        viewModelScope.launch {
            val response = repository.authReissue(refreshToken)
            authReissueResponse.value = response

            if (response.isSuccessful) {
                toastMessage.value = "토큰 재발급에 성공하셨습니다."
            } else {
                when (response.code()) {
                    401 -> refreshTokenExpiration.value = true
                    404 -> toastMessage.value = "예기치 못한 오류가 발생했습니다.\n고객센터에 문의해주세요."
                }
            }
        }
    }
}