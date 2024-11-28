package com.hayakai.ui.editprofile

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
import com.hayakai.databinding.ActivityEditProfileBinding
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.onboarding.OnboardingActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditProfileBinding

    private val sessionViewModel: SessionViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val editProfileViewModel: EditProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
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
        editProfileViewModel.getProfile().observe(this) { profile ->
            when (profile) {
                is MyResult.Loading -> {
//                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is MyResult.Success -> {
//                    binding.progressIndicator.visibility = View.GONE
                    profile.data.let {
                        binding.etFullName.setText(profile.data.name)
                        binding.etPhone.setText(profile.data.phone)
                        Glide.with(this)
                            .load(profile.data.image)
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
        binding.btnSave.setOnClickListener(this)
        binding.userImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_button -> {
                finish()
            }

            R.id.btn_save -> {
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show()
            }

            R.id.user_image -> {
                Toast.makeText(this, "Upload User Image", Toast.LENGTH_SHORT).show()
            }
        }
    }


}