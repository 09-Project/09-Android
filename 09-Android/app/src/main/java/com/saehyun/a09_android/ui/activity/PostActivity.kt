package com.saehyun.a09_android.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityPostBinding
import com.saehyun.a09_android.model.response.PostOtherResponse
import com.saehyun.a09_android.remote.RcOtherRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.auth.ReissueViewModel
import com.saehyun.a09_android.viewModel.auth.viewModelFactory.ReissueViewModelFactory
import com.saehyun.a09_android.viewModel.like.PostDeleteLikeViewModel
import com.saehyun.a09_android.viewModel.like.PostLikeViewModel
import com.saehyun.a09_android.viewModel.like.viewModelFactory.PostDeleteLikeViewModelFactory
import com.saehyun.a09_android.viewModel.like.viewModelFactory.PostLikeViewModelFactory
import com.saehyun.a09_android.viewModel.member.MemberShowViewModel
import com.saehyun.a09_android.viewModel.member.viewModelFactory.MemberShowViewModelFactory
import com.saehyun.a09_android.viewModel.post.PostDeleteViewModel
import com.saehyun.a09_android.viewModel.post.PostGetViewModel
import com.saehyun.a09_android.viewModel.post.PostOtherViewModel
import com.saehyun.a09_android.viewModel.post.viewModelFactory.PostDeleteViewModelFactory
import com.saehyun.a09_android.viewModel.post.viewModelFactory.PostGetViewModelFactory
import com.saehyun.a09_android.viewModel.post.viewModelFactory.PostOtherViewModelFactory


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

    private lateinit var memberShowViewModel: MemberShowViewModel
    private lateinit var memberShowViewModelFactory: MemberShowViewModelFactory

    private var productList = arrayListOf<PostOtherResponse>()

    private val repository: Repository = Repository()

    private var openChatLink: String ?= null

    private val TAG = "PostActivity"

    private var postId: Int ?= null

    private var memberId: Int ?= null

    private lateinit var reissueViewModelFactory: ReissueViewModelFactory
    private lateinit var reissueViewModel: ReissueViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView5.setOnClickListener {
            finish()
        }

        // Default Setting
        postId = intent.getStringExtra("postId").toString().toInt()

        reissueViewModelFactory = ReissueViewModelFactory(repository,applicationContext)
        reissueViewModel = ViewModelProvider(this,reissueViewModelFactory).get(ReissueViewModel::class.java)

        // memberShow
        memberShowViewModelFactory = MemberShowViewModelFactory(repository)
        memberShowViewModel = ViewModelProvider(this, memberShowViewModelFactory).get(MemberShowViewModel::class.java)

        // DeleteLike Post
        postDeleteLikeViewModelFactory = PostDeleteLikeViewModelFactory(repository)
        postDeleteLikeViewModel = ViewModelProvider(this, postDeleteLikeViewModelFactory).get(PostDeleteLikeViewModel::class.java)

        postDeleteLikeViewModel.memberDeleteLikeResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜 취소하기 성공")
            } else {
                when(it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    else -> ToastUtil.errorPrint(applicationContext)
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
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    409 -> {
                        ToastUtil.print(applicationContext, "찜 취소하기 성공!")
                        postDeleteLikeViewModel.memberDeleteLike(postId!!)
                        binding.ivPostHeart.setImageResource(R.drawable.ic_heart_off)
                    }
                    else -> ToastUtil.errorPrint(applicationContext)
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

                memberId = it.body()!!.member_info.member_id

                val price = it.body()!!.price
                if(price == 0) {
                    binding.tv092.visibility = View.GONE
                    binding.iv091.visibility = View.GONE
                    binding.textView16.visibility = View.GONE
                    binding.tvPostPrice.text = "무료나눔"
                } else {
                    binding.tvPostPrice.text = it.body()!!.price.toString()
                }

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
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    404 -> ToastUtil.print(applicationContext,"이미지가 존재하지 않습니다")
                    else -> ToastUtil.errorPrint(applicationContext)
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
                    binding.rvPostOtherProduct.adapter?.notifyDataSetChanged()
                }
            } else {
                when (it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    else -> ToastUtil.errorPrint(applicationContext)
                }
            }
        })

        binding.viewMember.setOnClickListener {
            val intent = Intent(this, MemberShowActivity::class.java)
            intent.putExtra("memberId", memberId.toString())
            startActivity(intent)
        }

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
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    404 -> ToastUtil.print(applicationContext, "존재하지 않는 상품입니다.")
                    else -> ToastUtil.errorPrint(applicationContext)
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