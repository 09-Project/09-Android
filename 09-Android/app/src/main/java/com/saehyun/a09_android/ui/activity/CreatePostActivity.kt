package com.saehyun.a09_android.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.saehyun.a09_android.databinding.ActivityCreatePostBinding
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.PostPostViewModel
import com.saehyun.a09_android.viewModelFactory.PostPostViewModelFactory
import com.saehyun.a09_android.viewModelFactory.PostViewModelFactory
import java.lang.Exception

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding

    private lateinit var postPostViewModel: PostPostViewModel

    private lateinit var postPostViewModelFactory: PostPostViewModelFactory

    private val OPEN_GALLERY = 1

    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        postPostViewModelFactory = PostPostViewModelFactory(repository)
        postPostViewModel =
            ViewModelProvider(this, postPostViewModelFactory).get(PostPostViewModel::class.java)

        postPostViewModel.authPostResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "글쓰기 등록이 완료되었습니다!")
                finish()
            } else {
                when (it.code()) {
                    400 -> ToastUtil.print(applicationContext, "토큰의 형태가 잘못되었습니다.")
                    401 -> ToastUtil.print(applicationContext, "토큰이 유효하지 않습니다.")
                    404 -> ToastUtil.print(applicationContext, "회원이 존재하지 않습니다.")
                    500 -> ToastUtil.print(applicationContext, "S3와의 연결이 실패되었습니다.")
                }
            }
        })

        val title = binding.etCreatePostTitle.text.toString()
        val content = binding.etCreatePostContent.text.toString()

        binding.ivCreatePostProductImage.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent .setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == OPEN_GALLERY) {

                var currentImageUrl : Uri? = data?.data

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
                    binding.ivCreatePostProductImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        else {
            ToastUtil.print(applicationContext, "Error!")
        }
    }
}