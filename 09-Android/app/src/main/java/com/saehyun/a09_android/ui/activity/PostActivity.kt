package com.saehyun.a09_android.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityPostBinding
import com.saehyun.a09_android.model.data.MemberInfo
import com.saehyun.a09_android.model.response.PostOtherResponse
import com.saehyun.a09_android.remote.RcOtherRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.*
import com.saehyun.a09_android.viewModelFactory.*


class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    private lateinit var postGetViewModel: PostGetViewModel
    private lateinit var postGetViewModelFactory: PostGetViewModelFactory

    private lateinit var postOtherViewModel: PostOtherViewModel
    private lateinit var postOtherViewModelFactory: PostOtherViewModelFactory

    private lateinit var postLikeViewModel: PostLikeViewModel
    private lateinit var postLikeViewModelFactory: PostLikeViewModelFactory

    private lateinit var postDeleteViewModel: PostDeleteViewModel
    private lateinit var postDeleteViewModelFactory: PostDeleteViewModelFactory

    private lateinit var postDeleteLikeViewModel: PostDeleteLikeViewModel
    private lateinit var postDeleteLikeViewModelFactory: PostDeleteLikeViewModelFactory

    private var productList = arrayListOf<PostOtherResponse>()

    private val repository: Repository = Repository()

    private var openChatLink: String ?= null

    private val TAG = "PostActivity"

    private var postId: Int ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default Setting
        postId = intent.getStringExtra("postId").toString().toInt()

        // DeleteLike Post
        postDeleteLikeViewModelFactory = PostDeleteLikeViewModelFactory(repository)
        postDeleteLikeViewModel = ViewModelProvider(this, postDeleteLikeViewModelFactory).get(PostDeleteLikeViewModel::class.java)

        postDeleteLikeViewModel.memberDeleteLikeResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜 취소하기 성공")
            } else {
                when(it.code()) {
                    401 -> {
                        // 토큰 만료
                    }
                    404 -> {
                        ToastUtil.print(applicationContext, "상품 또는 회원이 존재하지 않습니다.")
                    }
                }
            }
        })

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
                    409 -> {
                        ToastUtil.print(applicationContext, "찜 취소하기 성공!")
                        postDeleteLikeViewModel.memberDeleteLike(postId!!)
                        binding.ivPostHeart.setImageResource(R.drawable.ic_heart_off)
                    }
                }
            }
        })

        binding.viewLike.setOnClickListener {
            postLikeViewModel.authPostLikeSearch(postId!!)
        }

        // Post Get
        postGetViewModelFactory = PostGetViewModelFactory(repository)
        postGetViewModel = ViewModelProvider(this, postGetViewModelFactory).get(PostGetViewModel::class.java)

        postGetViewModel.authPostGetResponse.observe(this, Observer {
            if(it.isSuccessful) {
                openChatLink = it.body()!!.open_chat_link

                Glide.with(applicationContext)
                    .load(it.body()!!.image)
                    .into(binding.ivProduct)

                binding.tvMemberName.text = it.body()!!.member_info.member_name

                if(!(it.body()!!.member_info.member_profile.isNullOrBlank())) {
                    Glide.with(applicationContext)
                        .load(it.body()!!.member_info.member_profile)
                        .into(binding.ivMemberProfile)
                }

                if(it.body()!!.liked) {
                    Glide.with(applicationContext)
                        .load(R.drawable.ic_heart_on)
                        .into(binding.ivPostHeart)
                }

                if(it.body()!!.mine) {
                    myPost()
                } else {
                    otherPost()
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

        postGetViewModel.authGetPost(postId!!)

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

    private fun otherPost() {
        binding.viewChat.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("오픈채팅방으로 이동합니다.")

            builder.setPositiveButton("네"){dialogInterface, which ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(openChatLink))
                startActivity(intent)
            }
            builder.setNegativeButton("아니오") { dialog, id ->
                ToastUtil.print(applicationContext, "cancel")
            }

            builder.show()
        }
    }

    private fun myPost() {
        postDeleteViewModelFactory = PostDeleteViewModelFactory(repository)
        postDeleteViewModel = ViewModelProvider(this, postDeleteViewModelFactory).get(PostDeleteViewModel::class.java)

        postDeleteViewModel.postDeleteResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "삭제가 완료되었습니다.")
                finish()
            } else {
                when (it.code()) {
                    401 -> {
                        // 토큰 재발급
                    }
                    404 -> {
                        ToastUtil.print(applicationContext, "존재하지 않는 상품입니다.")
                    }
                    else -> {
                        ToastUtil.print(applicationContext, "Error!")
                    }
                }
            }
        })

        binding.tvSecond.text = "삭제하기"

        binding.ivSecond.setImageResource(R.drawable.ic_post_delete_circle)

        binding.viewChat.setOnClickListener {
            postDeleteViewModel.postDelete(postId.toString())
        }
    }

}