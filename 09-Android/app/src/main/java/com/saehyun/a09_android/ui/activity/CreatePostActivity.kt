package com.saehyun.a09_android.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.saehyun.a09_android.databinding.ActivityCreatePostBinding
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ACCESS_TOKEN
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.PostGroupBuyViewModel
import com.saehyun.a09_android.viewModel.PostSharingViewModel
import com.saehyun.a09_android.viewModel.ReissueViewModel
import com.saehyun.a09_android.viewModelFactory.PostGroupBuyViewModelFactory
import com.saehyun.a09_android.viewModelFactory.PostSharingViewModelFactory
import com.saehyun.a09_android.viewModelFactory.ReissueViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding

    private lateinit var postGroupBuyViewModel: PostGroupBuyViewModel
    private lateinit var postGroupBuyViewModelFactory: PostGroupBuyViewModelFactory

    private lateinit var postSharingViewModel: PostSharingViewModel
    private lateinit var postSharingViewModelFactory: PostSharingViewModelFactory

    private val OPEN_GALLERY = 1

    private var productImage: Boolean = false

    private lateinit var filetoUpload: MultipartBody.Part

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verifyStoragePermissions(this)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Repository()

        val reissueViewModelFactory = ReissueViewModelFactory(repository,applicationContext)
        val reissueViewModel = ViewModelProvider(this,reissueViewModelFactory).get(ReissueViewModel::class.java)

        binding.ibCreatePostBack.setOnClickListener {
            finish()
        }

        postGroupBuyViewModelFactory = PostGroupBuyViewModelFactory(repository)
        postGroupBuyViewModel =
            ViewModelProvider(this, postGroupBuyViewModelFactory).get(PostGroupBuyViewModel::class.java)

        postSharingViewModelFactory = PostSharingViewModelFactory(repository)
        postSharingViewModel =
                ViewModelProvider(this, postSharingViewModelFactory).get(PostSharingViewModel::class.java)

        postGroupBuyViewModel.authPostResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "글쓰기 등록이 완료되었습니다!")
                finish()
            } else {
                when (it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    else -> ToastUtil.errorPrint(applicationContext)
                }
            }
        })

        postSharingViewModel.authPostResponse.observe(this, Observer {
            if (it.isSuccessful) {
                ToastUtil.print(applicationContext, "글쓰기 등록이 완료되었습니다!")
                finish()
            } else {
                when (it.code()) {
                    401 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    else -> ToastUtil.errorPrint(applicationContext)
                }
            }
        })

        binding.ivCreatePostProductImage.setOnClickListener {
            openGallery()
        }

        binding.cbFree.setOnClickListener {
            if(binding.cbFree.isChecked) {
                binding.cbGroupBuy.isChecked = false
                binding.fmPrice.visibility = View.GONE
                binding.viewPrice.visibility = View.GONE
                binding.tvCreatePostPrice.visibility = View.GONE
            }
        }

        binding.cbGroupBuy.setOnClickListener {
            if(binding.cbGroupBuy.isChecked) {
                binding.cbFree.isChecked = false
                binding.fmPrice.visibility = View.VISIBLE
                binding.viewPrice.visibility = View.VISIBLE
                binding.tvCreatePostPrice.visibility = View.VISIBLE
            }
        }

        binding.etCreatePostContent.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvContentSize.text = "(${binding.etCreatePostContent.text.length}/40)"
            }
        })

        binding.tvCreatePostFinish.setOnClickListener {

            if(!(binding.cbFree.isChecked || binding.cbGroupBuy.isChecked)) {
                ToastUtil.print(applicationContext, "공동구매와, 무료나눔 중 하나를 선택해주세요!")
                return@setOnClickListener
            }

            if(binding.etCreatePostPrice.text.isEmpty() && binding.cbGroupBuy.isChecked) {
                ToastUtil.print(applicationContext, "가격을 선택해주세요!")
                return@setOnClickListener
            }

            if(binding.etCreatePostTitle.text.isEmpty()) {
                ToastUtil.print(applicationContext, "제목을 입력해주세요!")
                return@setOnClickListener
            }

            if(binding.etCreatePostContent.text.isEmpty()) {
                ToastUtil.print(applicationContext, "내용을 입력해주세요!")
                return@setOnClickListener
            }

            if(binding.etCreatePostTransactionRegion.text.isEmpty()) {
                ToastUtil.print(applicationContext, "거래지역을 입력해주세요!")
                return@setOnClickListener
            }

            if(binding.etCreatePostOpenLink.text.isEmpty()) {
                ToastUtil.print(applicationContext, "오픈채팅 링크를 입력해주세요!")
                return@setOnClickListener
            }

            if(productImage == false) {
                ToastUtil.print(applicationContext, "제품 사진을 등록해주세요!")
                return@setOnClickListener
            }


            val title: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.etCreatePostTitle.text.toString())
            val content: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.etCreatePostContent.text.toString())
            val price: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.etCreatePostPrice.text.toString())
            val transactionRegion: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.etCreatePostTransactionRegion.text.toString())
            val openChatLink: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.etCreatePostOpenLink.text.toString())

            if(binding.cbGroupBuy.isChecked) {
                postGroupBuyViewModel.authPost(title, content, price, transactionRegion, openChatLink, filetoUpload)
            } else {
                postSharingViewModel.authPostSharing(title, content, transactionRegion, openChatLink, filetoUpload)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, OPEN_GALLERY)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)  {

            val selectedImage = data?.data
            val photoUri = data?.data

            try {
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
                bitmap = rotateImage(bitmap, 90)

                binding.ivCreatePostProductImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val cursor = contentResolver.query(Uri.parse(selectedImage.toString()), null, null, null, null)!!
            cursor.moveToFirst()

            val mediaPath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))

            productImage = true

            getFile(mediaPath)
        } else {
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFile(mediaPath: String) {
        val file = File(mediaPath)

        val requestBody: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        filetoUpload = MultipartBody.Part.createFormData("image", file.name, requestBody)
    }


    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity?) {
        val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

}