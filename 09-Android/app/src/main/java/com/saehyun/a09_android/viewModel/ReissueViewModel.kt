package com.saehyun.a09_android.viewModel

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.response.AuthReissueResponse
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.ui.activity.LoginActivity
import com.saehyun.a09_android.util.ACCESS_TOKEN
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import kotlinx.coroutines.launch
import retrofit2.Response

class ReissueViewModel(private val repository: Repository, private val context: Context) : ViewModel() {
    val authReissueResponse : MutableLiveData<Response<AuthReissueResponse>> = MutableLiveData()

    fun authReissue(refreshToken: String) {
        viewModelScope.launch {
            val response = repository.authReissue(refreshToken)
            authReissueResponse.value = response

            if (response.isSuccessful) {
                REFRESH_TOKEN = response.body()?.refreshToken.toString()
                ACCESS_TOKEN = response.body()?.accessToken.toString()
            } else {
                when (response.code()) {
                    401 -> {
                        ToastUtil.print(context, "토큰이 만료되어 로그아웃 되었습니다.")

                        Handler().postDelayed({
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            ACCESS_TOKEN = "default"
                            REFRESH_TOKEN = "default"
                            context.startActivity(intent)
                        }, 2000L)

                    }
                    404 -> ToastUtil.print(context, "예기치 못한 오류가 발생했습니다.\n고객센터에 문의해주세요.")
                }
            }
        }
    }
}