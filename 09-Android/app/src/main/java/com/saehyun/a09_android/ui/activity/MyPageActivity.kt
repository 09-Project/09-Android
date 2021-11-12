package com.saehyun.a09_android.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.saehyun.a09_android.databinding.ActivityMyPageBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.model.response.MyPageResponse
import com.saehyun.a09_android.remote.MemberLikeRvAdapter
import com.saehyun.a09_android.remote.RcCompletedRvAdapter
import com.saehyun.a09_android.remote.RcOtherRvAdapter
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ACCESS_TOKEN
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.*
import com.saehyun.a09_android.viewModelFactory.*
import okhttp3.internal.wait

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding

    private var memberCompletedList = arrayListOf<PostValue>()
    private var memberInProgressList = arrayListOf<PostValue>()
    private var memberLikeList = arrayListOf<PostValue>()

    private lateinit var postLikeViewModel: PostLikeViewModel
    private lateinit var postLikeViewModelFactory: PostLikeViewModelFactory

    private lateinit var memberLikeViewModel: MemberLikeViewModel
    private lateinit var memberLikeViewModelFactory: MemberLikeViewModelFactory

    private lateinit var myPageViewModel: MyPageViewModel
    private lateinit var myPageViewModelFactory: MyPageViewModelFactory

    private lateinit var memberInProgressViewModel: MemberInProgressViewModel
    private lateinit var memberInProgressViewModelFactory: MemberInProgressViewModelFactory

    private lateinit var memberCompletedViewModel: MemberCompletedViewModel
    private lateinit var memberCompletedViewModelFactory: MemberCompletedViewModelFactory

    private lateinit var postDeleteLikeViewModel: PostDeleteLikeViewModel
    private lateinit var postDeleteLikeViewModelFactory: PostDeleteLikeViewModelFactory

    private var memberId: Int ?= null

    private val TAG = "MyPageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        // MyPage
        myPageViewModelFactory = MyPageViewModelFactory(repository)
        myPageViewModel = ViewModelProvider(this, myPageViewModelFactory).get(MyPageViewModel::class.java)

        myPageViewModel.myPageResponse.observe(this, Observer {
            if (it.isSuccessful) {
                memberId = it.body()!!.member_id.toInt()

                binding.tvMyPageName.text = it.body()!!.name

                if(!(it.body()!!.profile_url.isNullOrEmpty())) {
                    Glide.with(applicationContext)
                        .load(it.body()!!.profile_url)
                        .into(binding.ivMyPageProfile)
                }

                if(!(it.body()!!.introduction.isNullOrEmpty())) {
                    binding.tvMyPageContent.text = it.body()!!.introduction
                }

                binding.tvMypageAllpostscount.text = it.body()!!.all_posts_count

                binding.tvLikePostsCount.text = it.body()!!.like_posts_count

                binding.tvMypageGetlikescount.text = it.body()!!.get_likes_count

                binding.tvMypageCompletedpostscount.text = it.body()!!.completed_posts_count
            } else {
                when(it.code()) {
                    400 -> {
                        ToastUtil.print(applicationContext, "토큰의 형태가 잘못되었습니다.")
                    }
                    401 -> {
                        ToastUtil.print(applicationContext, "Access 토큰이 만료되었습니다.")
                    }
                    404 -> {
                        ToastUtil.print(applicationContext, "회원이 존재하지 않습니다.")
                    }
                }
            }
        })

        myPageViewModel.myPage()

        // Like Post
        postLikeViewModelFactory = PostLikeViewModelFactory(repository)
        postLikeViewModel = ViewModelProvider(this, postLikeViewModelFactory).get(PostLikeViewModel::class.java)

        postLikeViewModel.authPostLikeResponse.observe(this, {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜하기 성공!")
            } else {
                when(it.code()) {
                    400 -> ToastUtil.print(applicationContext, "Access 토큰의 형태가 잘못되었습니다.")
                    401 -> ToastUtil.print(applicationContext, "Access 토큰이 유효하지 않습니다.")
                    404 -> ToastUtil.print(applicationContext, "상품이나 회원이 존재하지 않습니다.")
                    409 -> ToastUtil.print(applicationContext, "찜이 이미 존재합니다.")
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
                    401 -> {
                        // 토큰 만료
                    }
                    404 -> {
                        ToastUtil.print(applicationContext, "상품 또는 회원이 존재하지 않습니다.")
                    }
                }
            }
        })

        // MemberLike
        memberLikeViewModelFactory = MemberLikeViewModelFactory(repository)
        memberLikeViewModel = ViewModelProvider(this, memberLikeViewModelFactory).get(MemberLikeViewModel::class.java)

        memberLikeViewModel.memberLikeResponse.observe(this, {
            if(it.isSuccessful) {
                Log.d(TAG, "onCreate: ${it.body()}")
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

        // Default RvSet
        binding.rvMyPage.adapter = RcProductRvAdapter(applicationContext, memberInProgressList, postLikeViewModel, postDeleteLikeViewModel)
        memberInProgressViewModel.memberInProgress(memberId.toString())

        // LogOut
        binding.tvLogOut.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            ACCESS_TOKEN = "default"
            REFRESH_TOKEN = "default"

            ToastUtil.print(applicationContext, "success")

            startActivity(intent)
        }

        // Set Tab
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("상품"))
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("찜한 상품"))
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("거래 내역"))

        binding.rvMyPage.layoutManager = GridLayoutManager(this, 2)
        binding.rvMyPage.setHasFixedSize(true)

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
}
