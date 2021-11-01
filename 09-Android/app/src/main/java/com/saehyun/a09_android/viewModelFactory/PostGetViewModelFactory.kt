
package com.saehyun.a09_android.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.viewModel.LoginViewModel
import com.saehyun.a09_android.viewModel.PostGetViewModel
import com.saehyun.a09_android.viewModel.PostViewModel
import com.saehyun.a09_android.viewModel.RegisterViewModel

class PostGetViewModelFactory(
    private val repository : Repository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostGetViewModel(repository) as T
    }
}