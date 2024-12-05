package com.hayakai.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hayakai.R
import com.hayakai.databinding.ActivityOnboardingBinding
import com.hayakai.navigation.BottomNavigation
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.createaccount.CreateAccountActivity
import com.hayakai.ui.login.LoginActivity
import com.hayakai.utils.ViewModelFactory

class OnboardingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityOnboardingBinding

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()
        setupViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewModel() {
        sessionViewModel.getSession().observe(this) {
            if (it.token.isNotEmpty()) {
                val intent = Intent(this@OnboardingActivity, BottomNavigation::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                val intent = Intent(this@OnboardingActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_register -> {
                val intent = Intent(this@OnboardingActivity, CreateAccountActivity::class.java)
                startActivity(intent)
            }
        }

    }


}