package com.saehyun.a09_android.viewModel.post.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.viewModel.post.PostSearchViewModel

class PostSearchViewModelFactory(
    private val repository : Repository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostSearchViewModel(repository) as T
    }
}