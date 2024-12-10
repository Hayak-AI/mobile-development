package com.hayakai.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hayakai.R
import com.hayakai.ui.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        val tvTagline = findViewById<TextView>(R.id.tv_tagline)
        val tvSubtitle = findViewById<TextView>(R.id.tv_subtitle)

        val taglineAnimationSet = AnimationSet(true).apply {
            addAnimation(TranslateAnimation(0f, 0f, 500f, 0f).apply {
                duration = 500
                fillAfter = true
            })
            addAnimation(AlphaAnimation(0.0f, 1.0f).apply {
                duration = 500
            })
        }
        tvTagline.startAnimation(taglineAnimationSet)

        val subtitleAnimationSet = AnimationSet(true).apply {
            addAnimation(TranslateAnimation(0f, 0f, 500f, 0f).apply {
                duration = 500
                startOffset = 1200
                fillAfter = true
            })
            addAnimation(AlphaAnimation(0.0f, 1.0f).apply {
                duration = 500
                startOffset = 1200
            })
        }
        tvSubtitle.startAnimation(subtitleAnimationSet)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        //Normal Handler is deprecated , so we have to change the code little bit

        // Handler().postDelayed({
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }, 100)
    }
}