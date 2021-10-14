package com.saehyun.a09_android.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.UiThread
import com.saehyun.a09_android.R
import com.saehyun.a09_android.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        splashAnimation()
    }

    @UiThread
    private fun splashAnimation() {
        val textAnim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_textview)
        binding.tvLogo.startAnimation(textAnim)
        binding.tvLore.startAnimation(textAnim)
        val imageAnim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_imageview)
        binding.ivLogo.startAnimation(imageAnim)

        imageAnim.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationEnd(animation: Animation) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.anim_splash_out_top, R.anim.anim_splash_in_down)
                finish()
            }

            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
    }
}