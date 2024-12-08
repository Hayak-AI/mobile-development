package com.hayakai.ui.splashscreen

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hayakai.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val tvTagline = findViewById<TextView>(R.id.tv_tagline)

        val translateAnimation = TranslateAnimation(0f, 0f, 500f, 0f).apply {
            duration = 1000
            fillAfter = true
        }

        val alphaAnimation = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 1000
        }

        tvTagline.startAnimation(translateAnimation)
        tvTagline.startAnimation(alphaAnimation)

        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100).apply {
            duration = 2000
        }
        animator.start()
    }
}