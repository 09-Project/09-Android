package com.saehyun.a09_android.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityLoginBinding
import com.saehyun.a09_android.repository.Repository
import com.saehyun.a09_android.util.ACCESS_TOKEN
import com.saehyun.a09_android.util.REFRESH_TOKEN
import com.saehyun.a09_android.util.ToastUtil
import com.saehyun.a09_android.viewModel.LoginViewModel
import com.saehyun.a09_android.viewModelFactory.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginTvRegister.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }

        binding.tvBtnJoinUs.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }

        val repository = Repository()
        val viewModelFactory = LoginViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        binding.tvLogin.setOnClickListener {
            val name = binding.etName.text.toString()
            val username = binding.etPassword.text.toString()

            if (name.isEmpty() || username.isEmpty()) {
                ToastUtil.print(applicationContext, "값을 모두 입력해주세요.")
                return@setOnClickListener
            }

            viewModel.authLogin(name, username)
            viewModel.authLoginResponse.observe(this, Observer {
                if (it.isSuccessful) {
                    ACCESS_TOKEN = it.body()?.accessToken.toString()
                    REFRESH_TOKEN = it.body()?.refreshToken.toString()

                    successLogin()
                } else {
                    when(it.code()) {
                        401 -> ToastUtil.print(applicationContext, "비밀번호가 일치하지 않습니다.\n다시 시도해주세요.")
                        404 -> ToastUtil.print(applicationContext, "회원이 존재하지 않습니다.\n다시 시도해주세요.")
                        else -> ToastUtil.print(applicationContext, "예기지 못한 오류가 발생했습니다.\n지속될 시 고객센터에 문의해주세요.")
                    }
                }
            })
        }
    }

    private fun successLogin() {
        ToastUtil.print(applicationContext, "로그인에 성공하셨습니다.")
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}