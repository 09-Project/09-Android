package com.saehyun.a09_android.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivitySearchBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.util.VIEW_SIZE
import com.saehyun.a09_android.viewModel.PostDeleteLikeViewModel
import com.saehyun.a09_android.viewModel.PostLikeViewModel
import com.saehyun.a09_android.viewModel.PostSearchViewModel
import com.saehyun.a09_android.viewModel.ReissueViewModel
import com.saehyun.a09_android.viewModelFactory.PostDeleteLikeViewModelFactory
import com.saehyun.a09_android.viewModelFactory.PostLikeViewModelFactory
import com.saehyun.a09_android.viewModelFactory.PostSearchViewModelFactory
import com.saehyun.a09_android.viewModelFactory.ReissueViewModelFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var searchViewModel: PostSearchViewModel

    private var productList = arrayListOf<PostValue>()

    private var currentPage = 0
    private var maxPage = 1

    private lateinit var keyword : String

    private lateinit var postLikeViewModel: PostLikeViewModel
    private lateinit var postLikeViewModelFactory: PostLikeViewModelFactory

    private lateinit var postDeleteLikeViewModel: PostDeleteLikeViewModel
    private lateinit var postDeleteLikeViewModelFactory: PostDeleteLikeViewModelFactory

    private lateinit var reissueViewModelFactory: ReissueViewModelFactory
    private lateinit var reissueViewModel: ReissueViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        val reissueViewModelFactory = ReissueViewModelFactory(repository,applicationContext)
        val reissueViewModel = ViewModelProvider(this,reissueViewModelFactory).get(ReissueViewModel::class.java)

        keyword = intent.getStringExtra("keyword").toString()

        binding.tvKeyword.text = keyword
        binding.seditText.setText(keyword)

        // like
        postLikeViewModelFactory = PostLikeViewModelFactory(repository)
        postLikeViewModel = ViewModelProvider(this, postLikeViewModelFactory).get(PostLikeViewModel::class.java)

        postLikeViewModel.authPostLikeResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜하기 성공!")
            } else {
                when(it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    404 -> ToastUtil.print(applicationContext, "상품이나 회원이 존재하지 않습니다.")
                    409 -> ToastUtil.print(applicationContext, "찜이 이미 존재합니다.")
                }
            }
        })

        // DeleteLike Post
        postDeleteLikeViewModelFactory = PostDeleteLikeViewModelFactory(repository)
        postDeleteLikeViewModel = ViewModelProvider(this, postDeleteLikeViewModelFactory).get(
            PostDeleteLikeViewModel::class.java)

        postDeleteLikeViewModel.memberDeleteLikeResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "찜 취소하기 성공")
            } else {
                when(it.code()) {
                    401 -> {
                        reissueViewModel.authReissue(REFRESH_TOKEN)
                    }
                    404 -> {
                        ToastUtil.print(applicationContext, "상품 또는 회원이 존재하지 않습니다.")
                    }
                }
            }
        })

        // Recyclerview Set
        binding.srvMainRcProduct.layoutManager = GridLayoutManager(this, 2)
        binding.srvMainRcProduct.setHasFixedSize(true)
        binding.srvMainRcProduct.adapter = RcProductRvAdapter(applicationContext, productList, postLikeViewModel, postDeleteLikeViewModel)

        // Page Set
        binding.ibSearchPageBack.setOnClickListener {
            --currentPage
            if(currentPage <= 1) {
                currentPage = maxPage-1
            }
            searchViewModel.authPostSearch(keyword, currentPage, VIEW_SIZE)
        }

        binding.ibSearchPageNext.setOnClickListener {
            ++currentPage
            if(currentPage >= maxPage) {
                currentPage = 0
            }
            searchViewModel.authPostSearch(keyword, currentPage, VIEW_SIZE)
        }

        // Post Search
        val postSearchViewModelFactory = PostSearchViewModelFactory(repository)
        searchViewModel = ViewModelProvider(this, postSearchViewModelFactory).get(PostSearchViewModel::class.java)

        searchViewModel.authPostSearchResponse.observe(this, Observer {
            if (it.isSuccessful) {
                val size = it.body()!!.posts.size

                val count = it.body()!!.count
                maxPage = count / 16
                if(count % 16 != 0) maxPage++
                if(maxPage == 0) maxPage++

                binding.tvSearchCurrentPage.text = "${currentPage+1}"
                binding.tvSearchMaxPage.text = " / ${maxPage}"

                productList.clear()

                if(count == 0) {
                    binding.clNoResult.visibility = View.VISIBLE
                    return@Observer
                } else {
                    binding.clNoResult.visibility = View.GONE
                }

                for(i: Int in 0 until size) {
                    val postValue: PostValue = it.body()!!.posts.get(i)
                    productList.add(postValue)
                    binding.srvMainRcProduct.adapter?.notifyDataSetChanged()
                }
            } else {
                when(it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    404 -> ToastUtil.print(applicationContext,"이미지가 존재하지 않습니다")
                }
            }
        })

        searchViewModel.authPostSearch(keyword, currentPage, VIEW_SIZE)

        // Search
        binding.seditText.setOnClickListener {
            val keyword = binding.seditText.text.toString()

            if(keyword.isEmpty()) {
                ToastUtil.print(applicationContext, "검색어를 입력해주세요")
                return@setOnClickListener
            }

            val intent: Intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("keyword", keyword)
            startActivity(intent)
        }

        // Drawer Menu
        binding.ibSearchMenu.setOnClickListener {
            if(binding.searchDrawer.isDrawerOpen(Gravity.RIGHT)) {
                binding.searchDrawer.closeDrawer(Gravity.RIGHT)
            } else {
                binding.searchDrawer.openDrawer(Gravity.RIGHT)
            }
        }

        binding.searchNavi.setNavigationItemSelectedListener {
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
    }
}