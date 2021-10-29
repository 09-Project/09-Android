package com.saehyun.a09_android.ui.activity

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import coil.api.load
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityMainBinding
import com.saehyun.a09_android.model.data.RcProductRvData
import com.saehyun.a09_android.model.response.PostResponse
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.task.ImageLoader
import com.saehyun.a09_android.task.URLtoBitmapTask
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.PostViewModel
import com.saehyun.a09_android.viewModel.ReissueViewModel
import com.saehyun.a09_android.viewModelFactory.PostViewModelFactory
import com.saehyun.a09_android.viewModelFactory.ReissueViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private lateinit var reissueViewModel: ReissueViewModel

    private lateinit var postViewModel: PostViewModel

    private lateinit var postViewModelFactory: PostViewModelFactory

    private var productList = arrayListOf<PostResponse>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.testImage.load("https://img.icons8.com/ios/50/000000/small-axe.png")

        val repository = Repository()

        binding.rvMainRcProduct.layoutManager = GridLayoutManager(this, 2)
        binding.rvMainRcProduct.setHasFixedSize(true)
        binding.rvMainRcProduct.adapter = RcProductRvAdapter(applicationContext, productList)

        postViewModelFactory = PostViewModelFactory(repository)
        postViewModel = ViewModelProvider(this, postViewModelFactory).get(PostViewModel::class.java)


        postViewModel.authPost()
        postViewModel.authPostResponse.observe(this, Observer {
            if (it.isSuccessful) {
                for(i: Int in 0 until it.body()!!.size) {
                    productList.add(it.body()!!.get(i))
                    binding.rvMainRcProduct.adapter?.notifyDataSetChanged()
                }
            } else {
                Log.d(TAG, "onResume: 실패")
            }
        })



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

    override fun onResume() {
        super.onResume()

    }
}