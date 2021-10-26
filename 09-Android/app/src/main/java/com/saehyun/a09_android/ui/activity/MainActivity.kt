package com.saehyun.a09_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityMainBinding
import com.saehyun.a09_android.model.data.RcProductRvData
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.ReissueViewModel
import com.saehyun.a09_android.viewModelFactory.ReissueViewModelFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var reissueViewModel: ReissueViewModel

    private var productList = arrayListOf<RcProductRvData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMainRcProduct.adapter = RcProductRvAdapter(applicationContext, productList)
        binding.rvMainRcProduct.layoutManager = GridLayoutManager(this, 2)
        binding.rvMainRcProduct.setHasFixedSize(true)
        binding.rvMainRcProduct.adapter?.notifyDataSetChanged()

        val repository = Repository()
        val reissueViewModelFactory = ReissueViewModelFactory(repository)
        reissueViewModel = ViewModelProvider(this, reissueViewModelFactory).get(ReissueViewModel::class.java)

        binding.button.setOnClickListener {
            reissueViewModel.authReissue(REFRESH_TOKEN)
            reissueViewModel.authReissueResponse.observe(this, Observer {
                if(it.isSuccessful) {
                    ToastUtil.print(applicationContext,"토큰 재발급에 성공하셨습니다.")
                } else {
                    when(it.code()) {
                        401 -> ToastUtil.print(applicationContext, "비밀번호가 일치하지 않습니다.")
                        404 -> ToastUtil.print(applicationContext, "회원이 존재하지 않습니다.")
                    }
                }
            })
        }
    }
}