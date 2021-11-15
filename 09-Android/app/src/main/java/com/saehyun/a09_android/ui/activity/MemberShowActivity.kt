package com.saehyun.a09_android.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.saehyun.a09_android.databinding.ActivityMyPageBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.remote.MemberLikeRvAdapter
import com.saehyun.a09_android.remote.RcCompletedRvAdapter
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ACCESS_TOKEN
import com.saehyun.a09_android.util.MEMBER_ID
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.auth.ReissueViewModel
import com.saehyun.a09_android.viewModel.auth.viewModelFactory.ReissueViewModelFactory
import com.saehyun.a09_android.viewModel.like.PostDeleteLikeViewModel
import com.saehyun.a09_android.viewModel.like.PostLikeViewModel
import com.saehyun.a09_android.viewModel.like.viewModelFactory.PostDeleteLikeViewModelFactory
import com.saehyun.a09_android.viewModel.like.viewModelFactory.PostLikeViewModelFactory
import com.saehyun.a09_android.viewModel.member.MemberCompletedViewModel
import com.saehyun.a09_android.viewModel.member.MemberInProgressViewModel
import com.saehyun.a09_android.viewModel.member.MemberLikeViewModel
import com.saehyun.a09_android.viewModel.member.MemberShowViewModel
import com.saehyun.a09_android.viewModel.member.viewModelFactory.MemberCompletedViewModelFactory
import com.saehyun.a09_android.viewModel.member.viewModelFactory.MemberInProgressViewModelFactory
import com.saehyun.a09_android.viewModel.member.viewModelFactory.MemberLikeViewModelFactory
import com.saehyun.a09_android.viewModel.member.viewModelFactory.MemberShowViewModelFactory

class MemberShowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding

    private var memberCompletedList = arrayListOf<PostValue>()
    private var memberInProgressList = arrayListOf<PostValue>()
    private var memberLikeList = arrayListOf<PostValue>()

    private lateinit var postLikeViewModel: PostLikeViewModel
    private lateinit var postLikeViewModelFactory: PostLikeViewModelFactory

    private lateinit var memberLikeViewModel: MemberLikeViewModel
    private lateinit var memberLikeViewModelFactory: MemberLikeViewModelFactory

    private lateinit var memberShowViewModel: MemberShowViewModel
    private lateinit var memberShowViewModelFactory: MemberShowViewModelFactory

    private lateinit var memberInProgressViewModel: MemberInProgressViewModel
    private lateinit var memberInProgressViewModelFactory: MemberInProgressViewModelFactory

    private lateinit var memberCompletedViewModel: MemberCompletedViewModel
    private lateinit var memberCompletedViewModelFactory: MemberCompletedViewModelFactory

    private lateinit var postDeleteLikeViewModel: PostDeleteLikeViewModel
    private lateinit var postDeleteLikeViewModelFactory: PostDeleteLikeViewModelFactory

    private var memberId: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        binding.imageView4.setOnClickListener {
            finish()
        }

        if(TextUtils.isEmpty(intent.getStringExtra("memberId"))) {
            memberId = MEMBER_ID.toString()
        } else {
            memberId = intent.getStringExtra("memberId")
        }

        val reissueViewModelFactory = ReissueViewModelFactory(repository,applicationContext)
        val reissueViewModel: ReissueViewModel = ViewModelProvider(this,reissueViewModelFactory).get(ReissueViewModel::class.java)

        binding.rvMyPage.layoutManager = GridLayoutManager(this, 2)
        binding.rvMyPage.setHasFixedSize(true)

        memberShowViewModelFactory = MemberShowViewModelFactory(repository)
        memberShowViewModel = ViewModelProvider(this, memberShowViewModelFactory).get(MemberShowViewModel::class.java)

        memberShowViewModel.memberShowResponse.observe(this, Observer {
            if (it.isSuccessful) {
                binding.tvMyPageName.text = it.body()!!.name

                if(!(it.body()!!.profile_url.isNullOrEmpty())) {
                    Glide.with(applicationContext)
                        .load(it.body()!!.profile_url)
                        .into(binding.ivMyPageProfile)
                }

                if(!(it.body()!!.introduction.isNullOrEmpty())) {
                    binding.tvMyPageContent.text = it.body()!!.introduction
                }

                if(MEMBER_ID == it.body()!!.member_id.toInt()) {
                    myProfile()
                } else {
                    otherProfile()
                }

                binding.tvMypageAllpostscount.text = it.body()!!.all_posts_count
                binding.tvLikePostsCount.text = it.body()!!.like_posts_count
                binding.tvMypageGetlikescount.text = it.body()!!.get_likes_count
                binding.tvMypageCompletedpostscount.text = it.body()!!.completed_posts_count

            } else {

                when(it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    else -> ToastUtil.errorPrint(applicationContext)
                }
            }

        })

        memberShowViewModel.memberShow(memberId.toString())

        // Like Post
        postLikeViewModelFactory = PostLikeViewModelFactory(repository)
        postLikeViewModel = ViewModelProvider(this, postLikeViewModelFactory).get(PostLikeViewModel::class.java)

        postLikeViewModel.authPostLikeResponse.observe(this, {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜하기 성공!")
            } else {
                when(it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    404 -> ToastUtil.print(applicationContext, "상품이나 회원이 존재하지 않습니다.")
                    409 -> ToastUtil.print(applicationContext, "찜이 이미 존재합니다.")
                    else -> ToastUtil.errorPrint(applicationContext)
                }
            }
        })

        // DeleteLike Post
        postDeleteLikeViewModelFactory = PostDeleteLikeViewModelFactory(repository)
        postDeleteLikeViewModel = ViewModelProvider(this, postDeleteLikeViewModelFactory).get(PostDeleteLikeViewModel::class.java)

        postDeleteLikeViewModel.memberDeleteLikeResponse.observe(this, {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜 취소하기 성공")
            } else {
                when(it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    else -> ToastUtil.errorPrint(applicationContext)
                }
            }
        })

        // MemberLike
        memberLikeViewModelFactory = MemberLikeViewModelFactory(repository)
        memberLikeViewModel = ViewModelProvider(this, memberLikeViewModelFactory).get(MemberLikeViewModel::class.java)

        memberLikeViewModel.memberLikeResponse.observe(this, {
            if(it.isSuccessful) {
                val size = it.body()!!.size

                for(i: Int in 0 until size) {
                    val postValue: PostValue = it.body()!!.get(i)
                    memberLikeList.add(postValue)

                    binding.rvMyPage.adapter?.notifyDataSetChanged()
                }
            }
        })

        // MemberInProgress
        memberInProgressViewModelFactory = MemberInProgressViewModelFactory(repository)
        memberInProgressViewModel = ViewModelProvider(this, memberInProgressViewModelFactory).get(MemberInProgressViewModel::class.java)

        memberInProgressViewModel.memberInProgressResponse.observe(this, {
            if(it.isSuccessful) {
                val size = it.body()!!.size

                for(i: Int in 0 until size) {
                    val postValue: PostValue = it.body()!!.get(i)
                    memberInProgressList.add(postValue)

                    binding.rvMyPage.adapter?.notifyDataSetChanged()
                }
            }
        })

        // MemberCompleted
        memberCompletedViewModelFactory = MemberCompletedViewModelFactory(repository)
        memberCompletedViewModel = ViewModelProvider(this, memberCompletedViewModelFactory).get(MemberCompletedViewModel::class.java)

        memberCompletedViewModel.memberCompletedResponse.observe(this, {
            if(it.isSuccessful) {
                val size = it.body()!!.size

                for(i: Int in 0 until size) {
                    val postValue: PostValue = it.body()!!.get(i)
                    memberCompletedList.add(postValue)

                    binding.rvMyPage.adapter?.notifyDataSetChanged()
                }
            }
        })

        // LogOut
        binding.tvLogOut.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            ACCESS_TOKEN = "default"
            REFRESH_TOKEN = "default"

            ToastUtil.print(applicationContext, "success")

            startActivity(intent)
        }
    }

    private fun myProfile() {
        binding.rvMyPage.adapter = RcProductRvAdapter(applicationContext, memberInProgressList, postLikeViewModel, postDeleteLikeViewModel)
        memberInProgressViewModel.memberInProgress(memberId.toString())

        // Set Tab
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("상품"))
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("찜한 상품"))
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("거래 내역"))

        binding.tabMyPage.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position) {
                    0 -> {
                        binding.rvMyPage.adapter = RcProductRvAdapter(applicationContext, memberInProgressList, postLikeViewModel, postDeleteLikeViewModel)
                        memberInProgressViewModel.memberInProgress(memberId.toString())
                    }
                    1 -> {
                        binding.rvMyPage.adapter = MemberLikeRvAdapter(applicationContext, memberLikeList)
                        memberLikeViewModel.memberLike()
                    }
                    2 -> {
                        binding.rvMyPage.adapter = RcCompletedRvAdapter(applicationContext, memberLikeList, postLikeViewModel)
                        memberCompletedViewModel.memberCompleted(memberId.toString())
                    }
                    else -> {
                        ToastUtil.print(applicationContext, "예기지 못한 오류입니다.")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    private fun otherProfile() {
        binding.textView9.text = "프로필"
        binding.tvLogOut.visibility = View.GONE
        binding.imageView9.visibility = View.GONE

        binding.rvMyPage.adapter = RcProductRvAdapter(applicationContext, memberInProgressList, postLikeViewModel, postDeleteLikeViewModel)
        memberInProgressViewModel.memberInProgress(memberId.toString())

        // Set Tab
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("상품"))
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("찜한 상품"))

        binding.tabMyPage.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position) {
                    0 -> {
                        binding.rvMyPage.adapter = RcProductRvAdapter(applicationContext, memberInProgressList, postLikeViewModel, postDeleteLikeViewModel)
                        memberInProgressViewModel.memberInProgress(memberId.toString())
                    }
                    1 -> {
                        binding.rvMyPage.adapter = MemberLikeRvAdapter(applicationContext, memberLikeList)
                        memberLikeViewModel.memberLike()
                    }
                    else -> {
                        ToastUtil.print(applicationContext, "예기지 못한 오류입니다.")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }
}
