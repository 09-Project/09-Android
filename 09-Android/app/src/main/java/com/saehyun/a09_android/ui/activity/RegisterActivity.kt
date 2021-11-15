package com.saehyun.a09_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.saehyun.a09_android.databinding.ActivityRegisterBinding
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.auth.RegisterViewModel
import com.saehyun.a09_android.viewModel.auth.viewModelFactory.RegisterViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var showPw = false

        binding.ibRegisterShowPw.setOnClickListener {
            showPw = !showPw

            if(showPw) {
                binding.registerEtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.registerEtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        val repository = Repository()
        val viewModelFactory = RegisterViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(RegisterViewModel::class.java)

        binding.tvJoinUs.setOnClickListener {
            val name = binding.registerEtName.text.toString()
            val username = binding.registerEtId.text.toString()
            val password = binding.registerEtPassword.text.toString()

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                ToastUtil.print(applicationContext, "값을 모두 입력해주세요.")
                return@setOnClickListener
            }

            viewModel.authSignUp(name, username, password)
            viewModel.authSignUpResponse.observe(this, Observer {
                if (it.isSuccessful) {
                    successJoin()
                } else {
                    when(it.code()) {
                        400 -> ToastUtil.print(applicationContext, "닉네임은 최대 10자까지 가능합니다.")
                        409 -> ToastUtil.print(applicationContext, "닉네임 또는 아이디가 이미 존재합니다.")
                        else -> ToastUtil.errorPrint(applicationContext)
                    }
                }
            })
        }

        binding.tvReturnLogin.setOnClickListener {
            finish()
        }
    }

    private fun successJoin() {
        ToastUtil.print(applicationContext, "회원가입에 성공하였습니다!")
        finish()
    }
 }