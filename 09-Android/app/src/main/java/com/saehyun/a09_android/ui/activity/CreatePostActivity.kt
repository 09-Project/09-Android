package com.saehyun.a09_android.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.saehyun.a09_android.databinding.ActivityCreatePostBinding
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.PostPostViewModel
import com.saehyun.a09_android.viewModelFactory.PostPostViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding

    private lateinit var postPostViewModel: PostPostViewModel

    private lateinit var postPostViewModelFactory: PostPostViewModelFactory

    private val OPEN_GALLERY = 1

    private var productImage: Boolean = false

    private lateinit var filetoUpload: MultipartBody.Part

    private var map: HashMap<String, RequestBody> = HashMap()

    private val TAG = "CreatePostActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verifyStoragePermissions(this)

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

        binding.ivCreatePostProductImage.setOnClickListener {
            openGallery()
        }

        binding.cbFree.setOnClickListener {
            if(binding.cbFree.isChecked) {
                binding.cbGroupBuy.isChecked = false
            }
        }

        binding.cbGroupBuy.setOnClickListener {
            if(binding.cbGroupBuy.isChecked) {
                binding.cbFree.isChecked = false
            }
        }

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

            val title = binding.etCreatePostTitle.text.toString()
            val content = binding.etCreatePostContent.text.toString()
            val price = binding.etCreatePostPrice.text.toString().toInt()
            val transactionRegion = binding.etCreatePostTransactionRegion.text.toString()
            val openChatLink = binding.etCreatePostOpenLink.text.toString()

            var rTitle: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
            var rContent: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), content)
            var rPrice: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), price.toString())
            var rTransactionRegion: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), transactionRegion)
            var rOpenChatLink: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), openChatLink)

            map.put("title", rTitle)
            map.put("content", rContent)
            map.put("price", rPrice)
            map.put("transactionRegion", rTransactionRegion)
            map.put("openChatLink", rOpenChatLink)


            if(binding.cbGroupBuy.isChecked) {
                postPostViewModel.authPost(filetoUpload, map)
            }
        }

        binding.button2.setOnClickListener {
            postPostViewModel.authPost(filetoUpload, map)
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
        val file: File = File(mediaPath)

        val requestBody: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        filetoUpload = MultipartBody.Part.createFormData("postImg", file.name, requestBody)
    }

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

}