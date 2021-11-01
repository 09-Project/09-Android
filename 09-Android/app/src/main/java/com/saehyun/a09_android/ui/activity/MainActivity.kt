package com.saehyun.a09_android.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityMainBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.util.VIEW_SIZE
import com.saehyun.a09_android.viewModel.PostViewModel
import com.saehyun.a09_android.viewModel.ReissueViewModel
import com.saehyun.a09_android.viewModelFactory.PostViewModelFactory
import com.saehyun.a09_android.viewModelFactory.ReissueViewModelFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private lateinit var reissueViewModel: ReissueViewModel

    private lateinit var postViewModel: PostViewModel

    private lateinit var postViewModelFactory: PostViewModelFactory

    private var productList = arrayListOf<PostValue>()

    private var currentPage = 0
    private var maxPage = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        // Recyclerview Set
        binding.rvMainRcProduct.layoutManager = GridLayoutManager(this, 2)
        binding.rvMainRcProduct.setHasFixedSize(true)
        binding.rvMainRcProduct.adapter = RcProductRvAdapter(applicationContext, productList)

        // Page Set
        binding.MainibBack.setOnClickListener {
            --currentPage
            if(currentPage <= 1) {
                ToastUtil.print(applicationContext, "첫 페이지 입니다.")
                return@setOnClickListener
            }
            postViewModel.authPost(currentPage, VIEW_SIZE)
        }

        binding.MainibNext.setOnClickListener {
            ++currentPage
            if(currentPage >= maxPage) {
                currentPage = 0
            }
            postViewModel.authPost(currentPage, VIEW_SIZE)
        }

        // Get Post
        postViewModelFactory = PostViewModelFactory(repository)
        postViewModel = ViewModelProvider(this, postViewModelFactory).get(PostViewModel::class.java)

        postViewModel.authPost(currentPage, VIEW_SIZE)

        postViewModel.authPostResponse.observe(this, Observer {
            if (it.isSuccessful) {
                val size = it.body()!!.posts.size

                val count = it.body()!!.count
                maxPage = count / 16
                if(count % 16 != 0) maxPage++
                if(maxPage == 0) maxPage = 1
                binding.tvCurrentPage.text = "${currentPage+1}"
                binding.tvMaxPage.text = " / ${maxPage}"

                productList.clear()
                for(i: Int in 0 until size) {
                    val postValue: PostValue = it.body()!!.posts.get(i)
                    productList.add(postValue)
                    binding.rvMainRcProduct.adapter?.notifyDataSetChanged()
                }
            } else {
                when(it.code()) {
                    404 -> ToastUtil.print(applicationContext,"이미지가 존재하지 않습니다")
                }
            }
        })

        // Token Reissue
        val reissueViewModelFactory = ReissueViewModelFactory(repository)
        val reissueViewModel: ReissueViewModel = ViewModelProvider(this, reissueViewModelFactory).get(ReissueViewModel::class.java)

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

        // Drawer Menu
        binding.ibMainMenu.setOnClickListener {
            if(binding.mainDrawer.isDrawerOpen(Gravity.RIGHT)) {
                binding.mainDrawer.closeDrawer(Gravity.RIGHT)
            } else {
                binding.mainDrawer.openDrawer(Gravity.RIGHT)
            }
        }

        binding.mainNavi.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.menuHome -> ToastUtil.print(applicationContext, "1")
                R.id.menuMyPage -> ToastUtil.print(applicationContext, "2")
                R.id.menuPost -> ToastUtil.print(applicationContext, "3")
            }

            binding.mainDrawer.closeDrawer(GravityCompat.END)
            true
        }

        binding.ibMainSearch.setOnClickListener {
            val keyword = binding.etMainSearch.text.toString()

            if(keyword.isEmpty()) {
                ToastUtil.print(applicationContext, "검색어를 입력해주세요")
                return@setOnClickListener
            }

            val intent: Intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("keyword", keyword)
            startActivity(intent)
        }
    }
}