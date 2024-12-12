package com.hayakai.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hayakai.R
import com.hayakai.ui.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                startOffset = 800
                fillAfter = true
            })
            addAnimation(AlphaAnimation(0.0f, 1.0f).apply {
                duration = 500
                startOffset = 800
            })
        }
        tvSubtitle.startAnimation(subtitleAnimationSet)

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Add a listener to update the behavior of the toggle fullscreen button when
        // the system bars are hidden or revealed.
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            // You can hide the caption bar even when the other system bars are visible.
            // To account for this, explicitly check the visibility of navigationBars()
            // and statusBars() rather than checking the visibility of systemBars().
            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())
            ) {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }


        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        //Normal Handler is deprecated , so we have to change the code little bit

        // Handler().postDelayed({
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500)
    }
}