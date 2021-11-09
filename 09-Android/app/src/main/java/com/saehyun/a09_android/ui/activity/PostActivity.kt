package com.saehyun.a09_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityPostBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.model.response.PostOtherResponse
import com.saehyun.a09_android.remote.RcOtherRvAdapter
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.PostGetViewModel
import com.saehyun.a09_android.viewModel.PostLikeViewModel
import com.saehyun.a09_android.viewModel.PostOtherViewModel
import com.saehyun.a09_android.viewModelFactory.PostGetViewModelFactory
import com.saehyun.a09_android.viewModelFactory.PostLikeViewModelFactory
import com.saehyun.a09_android.viewModelFactory.PostOtherViewModelFactory

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    private lateinit var postGetViewModel: PostGetViewModel
    private lateinit var postGetViewModelFactory: PostGetViewModelFactory

    private lateinit var postOtherViewModel: PostOtherViewModel
    private lateinit var postOtherViewModelFactory: PostOtherViewModelFactory

    private lateinit var postLikeViewModel: PostLikeViewModel
    private lateinit var postLikeViewModelFactory: PostLikeViewModelFactory

    private var productList = arrayListOf<PostOtherResponse>()

    private val TAG = "PostActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default Setting
        var repository = Repository()

        val postId = intent.getStringExtra("postId").toString().toInt()

        // Like Post
        postLikeViewModelFactory = PostLikeViewModelFactory(repository)
        postLikeViewModel = ViewModelProvider(this, postLikeViewModelFactory).get(PostLikeViewModel::class.java)

        postLikeViewModel.authPostLikeResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜하기 성공!")
                binding.ivPostHeart.setImageResource(R.drawable.ic_heart_on)
            } else {
                when(it.code()) {
                    400 -> ToastUtil.print(applicationContext, "Access 토큰의 형태가 잘못되었습니다.")
                    401 -> ToastUtil.print(applicationContext, "Access 토큰이 유효하지 않습니다.")
                    404 -> ToastUtil.print(applicationContext, "상품이나 회원이 존재하지 않습니다.")
                    409 -> ToastUtil.print(applicationContext, "찜이 이미 존재합니다.")
                }
            }
        })

        binding.viewLike.setOnClickListener {
            postLikeViewModel.authPostLikeSearch(postId.toInt())
        }

        // Post Get
        postGetViewModelFactory = PostGetViewModelFactory(repository)
        postGetViewModel = ViewModelProvider(this, postGetViewModelFactory).get(PostGetViewModel::class.java)

        postGetViewModel.authPostGetResponse.observe(this, Observer {
            if(it.isSuccessful) {
                Glide.with(applicationContext)
                    .load(it.body()!!.image)
                    .into(binding.ivProduct)

                binding.tvMemberName.text = it.body()!!.member_name

                if(!(it.body()!!.member_profile.isNullOrBlank())) {
                    Glide.with(applicationContext)
                        .load(it.body()!!.member_profile)
                        .into(binding.ivMemberProfile)
                }

                if(it.body()!!.liked) {
                    Glide.with(applicationContext)
                        .load(R.drawable.ic_heart_on)
                        .into(binding.ivPostHeart)
                }

                binding.tvPostTItle.text = it.body()!!.title
                binding.tvPostContent.text = it.body()!!.content
                binding.tvPostTransactionRegion.text = it.body()!!.transaction_region
            } else {
                when(it.code()) {
                    404 -> ToastUtil.print(applicationContext,"이미지가 존재하지 않습니다")
                }
            }
        })

        postGetViewModel.authGetPost(postId)

        // Recyclerview set
        binding.rvPostOtherProduct.layoutManager = GridLayoutManager(this, 3)
        binding.rvPostOtherProduct.setHasFixedSize(true)
        binding.rvPostOtherProduct.adapter = RcOtherRvAdapter(applicationContext, productList)

        // 다른 상품
        postOtherViewModelFactory = PostOtherViewModelFactory(repository)
        postOtherViewModel = ViewModelProvider(this, postOtherViewModelFactory).get(PostOtherViewModel::class.java)

        postOtherViewModel.authPostOtherResponse.observe(this, Observer {
            if (it.isSuccessful) {
                productList.clear()
                for (i: Int in 0..5) {
                    productList.add(it.body()!!.get(i))
                    Log.d(TAG, "onCreate: ${it.body()}")
                    binding.rvPostOtherProduct.adapter?.notifyDataSetChanged()
                }
            } else {
                when (it.code()) {
                    401 -> ToastUtil.print(applicationContext, "비밀번호가 일치하지 않습니다.")
                    404 -> ToastUtil.print(applicationContext, "회원이 존재하지 않습니다.")
                }
            }
        })

        postOtherViewModel.authPostOther()


    }
}