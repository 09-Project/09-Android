package com.saehyun.a09_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

        private lateinit var binding: ActivityRegisterBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityRegisterBinding.inflate(layoutInflater)
            setContentView(binding.root)
    }
}