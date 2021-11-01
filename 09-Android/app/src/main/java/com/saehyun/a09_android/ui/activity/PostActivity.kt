package com.saehyun.a09_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.saehyun.a09_android.databinding.ActivityPostBinding
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.PostGetViewModel
import com.saehyun.a09_android.viewModelFactory.PostGetViewModelFactory

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    private lateinit var postGetViewModel: PostGetViewModel

    private lateinit var postGetViewModelFactory: PostGetViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        postGetViewModelFactory = PostGetViewModelFactory(repository)
        postGetViewModel = ViewModelProvider(this, postGetViewModelFactory).get(PostGetViewModel::class.java)

        postGetViewModel.authPostGetResponse.observe(this, Observer {
            if(it.isSuccessful) {
                Glide.with(applicationContext)
                    .load(it.body()!!.image)
                    .into(binding.ivProduct)
                binding.tvMemberName.text = it.body()!!.member_name

                Glide.with(applicationContext)
                        .load(it.body()!!.member_profile)
                        .into(binding.ivMemberProfile)

                binding.tvPostTItle.text = it.body()!!.title
                binding.tvPostContent.text = it.body()!!.content
                binding.tvPostTransactionRegion.text = it.body()!!.transaction_region
            } else {
                when(it.code()) {
                    404 -> ToastUtil.print(applicationContext,"이미지가 존재하지 않습니다")
                }
            }
        })

        val postId = intent.getStringExtra("postId").toString().toInt()
        postGetViewModel.authGetPost(postId)
    }
}