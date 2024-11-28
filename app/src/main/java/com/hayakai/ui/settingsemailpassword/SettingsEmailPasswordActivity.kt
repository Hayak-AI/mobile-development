package com.hayakai.ui.settingsemailpassword

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hayakai.R
import com.hayakai.databinding.ActivitySettingsEmailPasswordBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class SettingsEmailPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySettingsEmailPasswordBinding

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val settingsEmailPasswordViewModel: SettingsEmailPasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsEmailPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        sessionViewModel.getSession().observe(this) {
            if (it.token.isEmpty()) {
                val intent = Intent(this, OnboardingActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                setupViewModel()
            }
        }

        setupAction()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewModel() {
        settingsEmailPasswordViewModel.getProfile().observe(this) { profile ->
            when (profile) {
                is MyResult.Loading -> {
                    // Show loading
                }

                is MyResult.Success -> {
                    profile.data.let {
                        binding.etEmail.setText(it.email)
                    }
                }

                is MyResult.Error -> {
                    Toast.makeText(this, profile.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.btnSave.setOnClickListener(this)
        binding.backButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnSave -> {
                // Save button clicked
            }

            binding.backButton -> {
                finish()
            }
        }
    }


}