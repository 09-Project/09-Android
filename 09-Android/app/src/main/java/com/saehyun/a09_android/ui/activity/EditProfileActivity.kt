package com.saehyun.a09_android.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.view.isEmpty
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.saehyun.a09_android.databinding.ActivityEditProfileBinding
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.auth.ReissueViewModel
import com.saehyun.a09_android.viewModel.auth.viewModelFactory.ReissueViewModelFactory
import com.saehyun.a09_android.viewModel.member.MemberInformationViewModel
import com.saehyun.a09_android.viewModel.member.viewModelFactory.MemberInformationViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding

    private val OPEN_GALLERY = 1

    private var profileImage: Boolean = false

    private lateinit var filetoUpload: MultipartBody.Part

    private lateinit var memberInformationViewModel: MemberInformationViewModel
    private lateinit var memberInformationViewModelFactory: MemberInformationViewModelFactory

    private lateinit var reissueViewModel: ReissueViewModel
    private lateinit var reissueViewModelFactory: ReissueViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verifyStoragePermissions(this)

        val repository = Repository()

        reissueViewModelFactory = ReissueViewModelFactory(repository, applicationContext)
        reissueViewModel = ViewModelProvider(this, reissueViewModelFactory).get(ReissueViewModel::class.java)

        memberInformationViewModelFactory = MemberInformationViewModelFactory(repository)
        memberInformationViewModel = ViewModelProvider(this, memberInformationViewModelFactory).get(MemberInformationViewModel::class.java)

        memberInformationViewModel.memberInformationResponse.observe(this, {
            if(it.isSuccessful) {
                ToastUtil.print(applicationContext, "프로필 수정이 완료되었습니다!")
                finish()
            } else {
                when(it.code()) {
                    201 -> reissueViewModel.authReissue(REFRESH_TOKEN)
                    else -> ToastUtil.print(applicationContext, "Error!")
                }
            }
        })

        binding.imageView14.setOnClickListener {
            openGallery()
        }

        binding.tvEditChange.setOnClickListener {
            if(binding.editText.text.isEmpty()) {
                ToastUtil.print(applicationContext, "닉네임을 입력해주세요")
            }
            if(profileImage == false) {
                ToastUtil.print(applicationContext, "프로필 이미지를 변경해주세요.")
            }

            val name: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.editText.text.toString())
            val introduction: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.etIntroduce.text.toString())

            memberInformationViewModel.memberInformation(name, introduction, filetoUpload)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                bitmap = TransformationUtils.rotateImage(bitmap, 90)

                binding.imageView14.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val cursor = contentResolver.query(Uri.parse(selectedImage.toString()), null, null, null, null)!!
            cursor.moveToFirst()

            val mediaPath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))

            profileImage = true

            getFile(mediaPath)
        } else {
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFile(mediaPath: String) {
        val file = File(mediaPath)

        val requestBody: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        filetoUpload = MultipartBody.Part.createFormData("profileUrl", file.name, requestBody)
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