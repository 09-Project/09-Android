package com.saehyun.a09_android.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.viewModel.*

class PostPostViewModelFactory(
    private val repository : Repository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostPostViewModel(repository) as T
    }
}