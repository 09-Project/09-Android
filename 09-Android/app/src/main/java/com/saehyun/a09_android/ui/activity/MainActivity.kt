package com.saehyun.a09_android.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityMainBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.*
import com.saehyun.a09_android.viewModel.*
import com.saehyun.a09_android.viewModelFactory.*


class   MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private lateinit var postViewModel: PostViewModel
    private lateinit var postViewModelFactory: PostViewModelFactory

    private lateinit var postLikeViewModel: PostLikeViewModel
    private lateinit var postLikeViewModelFactory: PostLikeViewModelFactory

    private lateinit var postDeleteLikeViewModel: PostDeleteLikeViewModel
    private lateinit var postDeleteLikeViewModelFactory: PostDeleteLikeViewModelFactory

    private var productList = arrayListOf<PostValue>()

    private var currentPage = 0
    private var maxPage = 1

    private var doubleBackToExit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        // Token Reissue
        val reissueViewModelFactory = ReissueViewModelFactory(repository, applicationContext)
        val reissueViewModel: ReissueViewModel = ViewModelProvider(this, reissueViewModelFactory).get(ReissueViewModel::class.java)

        // Page Set
        binding.MainibBack.setOnClickListener {
            --currentPage
            if(currentPage <= 1) {
                currentPage = maxPage-1
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

        // Like Post
        postLikeViewModelFactory = PostLikeViewModelFactory(repository)
        postLikeViewModel = ViewModelProvider(this, postLikeViewModelFactory).get(PostLikeViewModel::class.java)

        postLikeViewModel.authPostLikeResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜하기 성공!")
            } else {
                when(it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    409 -> ToastUtil.print(applicationContext, "찜이 이미 존재합니다.")
                    else -> ToastUtil.errorPrint(applicationContext)
                }
            }
        })

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

        // Recyclerview Set
        binding.rvMainRcProduct.layoutManager = GridLayoutManager(this, 2)
        binding.rvMainRcProduct.setHasFixedSize(true)
        binding.rvMainRcProduct.adapter = RcProductRvAdapter(applicationContext, productList, postLikeViewModel, postDeleteLikeViewModel)

        // Get Post
        postViewModelFactory = PostViewModelFactory(repository)
        postViewModel = ViewModelProvider(this, postViewModelFactory).get(PostViewModel::class.java)

        postViewModel.authPostResponse.observe(this, Observer {
            if (it.isSuccessful) {
                MEMBER_ID = it.body()!!.member_id

                val size = it.body()!!.posts.size

                val count = it.body()!!.count
                maxPage = count / 16
                if (count % 16 != 0) maxPage++
                if (maxPage == 0) maxPage = 1
                binding.tvCurrentPage.text = "${currentPage + 1}"
                binding.tvMaxPage.text = " / ${maxPage}"

                productList.clear()

                for (i: Int in 0 until size) {
                    val postValue: PostValue = it.body()!!.posts.get(i)
                    productList.add(postValue)
                    binding.rvMainRcProduct.adapter?.notifyDataSetChanged()
                }
            } else {
                when (it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    404 -> ToastUtil.print(applicationContext, "이미지가 존재하지 않습니다")
                    else -> ToastUtil.errorPrint(applicationContext)
                }
            }
        })

        // Drawer Menu
        binding.ibMainMenu.setOnClickListener {
            if(binding.mainDrawer.isDrawerOpen(Gravity.RIGHT)) {
                binding.mainDrawer.closeDrawer(Gravity.RIGHT)
            } else {
                binding.mainDrawer.openDrawer(Gravity.RIGHT)
            }
        }

        binding.mainNavi.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuPost -> {
                    startActivity(Intent(applicationContext, CreatePostActivity::class.java))
                    return@setNavigationItemSelectedListener true
                }
                R.id.menuMyPage -> {
                    startActivity(Intent(applicationContext, MemberShowActivity::class.java))
                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener false
            }
        }

        binding.ibMainSearch.setOnClickListener {
            val keyword = binding.etMainSearch.text.toString()

            if(keyword.isEmpty()) {
                ToastUtil.print(applicationContext, "검색어를 입력해주세요")
                return@setOnClickListener
            }

            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("keyword", keyword)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        postViewModel.authPost(currentPage, VIEW_SIZE)
    }

    override fun onBackPressed() {
        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, "종료하시려면 뒤로가기를 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
            }
        }
    }

    fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }
}