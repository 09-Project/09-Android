package com.saehyun.a09_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivitySearchBinding
import com.saehyun.a09_android.model.data.PostValue
import com.saehyun.a09_android.remote.RcProductRvAdapter
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.util.VIEW_SIZE
import com.saehyun.a09_android.viewModel.PostSearchViewModel
import com.saehyun.a09_android.viewModelFactory.PostSearchViewModelFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var searchViewModel: PostSearchViewModel

    private var productList = arrayListOf<PostValue>()

    private var currentPage = 0
    private var maxPage = 1

    private lateinit var keyword : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        keyword = intent.getStringExtra("keyword").toString()

        binding.tvKeyword.text = keyword
        binding.seditText.setText(keyword)

        // Recyclerview Set
        binding.srvMainRcProduct.layoutManager = GridLayoutManager(this, 2)
        binding.srvMainRcProduct.setHasFixedSize(true)
        binding.srvMainRcProduct.adapter = RcProductRvAdapter(applicationContext, productList)

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


        val postSearchViewModelFactory = PostSearchViewModelFactory(repository)
        searchViewModel = ViewModelProvider(this, postSearchViewModelFactory).get(PostSearchViewModel::class.java)

        searchViewModel.authPostSearchResponse.observe(this, Observer {
            if (it.isSuccessful) {
                val size = it.body()!!.posts.size

                val count = it.body()!!.count
                maxPage = count / 16
                if(count % 16 != 0) maxPage++
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
                    404 -> ToastUtil.print(applicationContext,"이미지가 존재하지 않습니다")
                }
            }
        })

        searchViewModel.authPostSearch(keyword, currentPage, VIEW_SIZE)
    }
}