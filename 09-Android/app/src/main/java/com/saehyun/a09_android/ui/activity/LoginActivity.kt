package com.saehyun.a09_android.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

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

        // Temp
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }
}