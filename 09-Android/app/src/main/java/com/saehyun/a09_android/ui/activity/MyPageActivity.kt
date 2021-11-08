package com.saehyun.a09_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.saehyun.a09_android.databinding.ActivityMyPageBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.model.response.MyPageResponse
import com.saehyun.a09_android.remote.RcOtherRvAdapter
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.PostLikeViewModel
import com.saehyun.a09_android.viewModelFactory.PostLikeViewModelFactory

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding

    private var productList = arrayListOf<PostValue>()

    private lateinit var postLikeViewModel: PostLikeViewModel
    private lateinit var postLikeViewModelFactory: PostLikeViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        // Like Post
        postLikeViewModelFactory = PostLikeViewModelFactory(repository)
        postLikeViewModel = ViewModelProvider(this, postLikeViewModelFactory).get(PostLikeViewModel::class.java)

        postLikeViewModel.authPostLikeResponse.observe(this, Observer {
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

        // Set Tab
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("상품"))
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("찜한 상품"))
        binding.tabMyPage.addTab(binding.tabMyPage.newTab().setText("거래 내역"))


        binding.rvMyPage.layoutManager = GridLayoutManager(this, 3)
        binding.rvMyPage.setHasFixedSize(true)

        binding.tabMyPage.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position) {
                    0 -> {
                        binding.rvMyPage.adapter = RcProductRvAdapter(applicationContext, productList, postLikeViewModel)


                    }
                    1 -> {
                        ToastUtil.print(applicationContext, "click 2")
                    }
                    2 -> {
                        ToastUtil.print(applicationContext, "click 3")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }
}
