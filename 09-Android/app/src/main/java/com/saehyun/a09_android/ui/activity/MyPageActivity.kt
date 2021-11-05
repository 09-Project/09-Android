package com.saehyun.a09_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.saehyun.a09_android.databinding.ActivityMyPageBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.model.response.MyPageResponse
import com.saehyun.a09_android.remote.RcOtherRvAdapter
import com.saehyun.a09_android.remote.RcProductRvAdapter

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding

    private var productList = arrayListOf<MyPageResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMyPage.layoutManager = GridLayoutManager(this, 3)
        binding.rvMyPage.setHasFixedSize(true)
//        binding.rvMyPage.adapter = RcOtherRvAdapter(applicationContext, productList)

        binding.tabMyPage.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position) {
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

        })
    }
}
