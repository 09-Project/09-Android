package com.saehyun.a09_android.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saehyun.a09_android.model.response.MemberShowResponse
import com.saehyun.a09_android.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MemberShowViewModel(private val repository: Repository) : ViewModel() {
    val memberShowResponse : MutableLiveData<Response<MemberShowResponse>> = MutableLiveData()

    fun memberShow(memberId: String) {
        viewModelScope.launch {
            val response = repository.memberShow(memberId)
            memberShowResponse.value = response
        }
    }
}