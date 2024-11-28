package com.hayakai.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.hayakai.R
import com.hayakai.databinding.ActivityProfileBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.editprofile.EditProfileActivity
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.ui.settingsemailpassword.SettingsEmailPasswordActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityProfileBinding

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
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
        profileViewModel.getSettings().observe(this) { settings ->
            when (settings) {
                is MyResult.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is MyResult.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    settings.data.let {
                        binding.darkModeSwitch.isChecked = it.darkMode
                        binding.voiceDetectionSwitch.isChecked = it.voiceDetection
                        binding.trackMyLocationSwitch.isChecked = it.locationTracking
                    }
                }

                is MyResult.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this, settings.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        profileViewModel.getProfile().observe(this) { profile ->
            when (profile) {
                is MyResult.Loading -> {
//                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is MyResult.Success -> {
//                    binding.progressIndicator.visibility = View.GONE
                    profile.data.let {
                        binding.userName.text = it.name
                        binding.userEmail.text = it.email
                        Glide.with(this)
                            .load(it.image)
                            .placeholder(R.drawable.mdi_user_outline)
                            .into(binding.userImage)
                    }
                }

                is MyResult.Error -> {
//                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this, profile.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.backButton.setOnClickListener(this)
        binding.settingsPersonalInformation.setOnClickListener(this)
        binding.settingsEmailPassword.setOnClickListener(this)
        binding.settingsLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_button -> {
                finish()
            }

            R.id.settings_personal_information -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.settings_email_password -> {
                val intent = Intent(this, SettingsEmailPasswordActivity::class.java)
                startActivity(intent)
            }

            R.id.settings_logout -> {
                sessionViewModel.logout()
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}