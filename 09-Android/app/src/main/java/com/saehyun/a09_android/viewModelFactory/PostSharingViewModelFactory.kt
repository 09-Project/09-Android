package com.saehyun.a09_android.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.viewModel.*

class PostSharingViewModelFactory(
    private val repository : Repository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostSharingViewModel(repository) as T
    }
}